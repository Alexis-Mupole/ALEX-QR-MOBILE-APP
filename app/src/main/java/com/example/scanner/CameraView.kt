package com.example.scanner

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    isTorchEnabled: Boolean = false,
    isPaused: Boolean = false,
    powerSaverMode: Boolean = false,
    onBarcodeDetected: (String) -> Unit,
    onCameraUnavailable: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    val lastAnalyzedTime = remember { AtomicLong(0L) }
    val isProcessingFrame = remember { AtomicBoolean(false) }

    // Instant & reliable hardware torch handling
    LaunchedEffect(isTorchEnabled, isPaused, cameraInstance) {
        cameraInstance?.let { cam ->
            try {
                if (cam.cameraInfo.hasFlashUnit()) {
                    // Turn off flash immediately if paused or user toggled off
                    val targetTorch = isTorchEnabled && !isPaused
                    cam.cameraControl.enableTorch(targetTorch)
                }
            } catch (e: Exception) {
                Log.e("CameraView", "Failed to toggle torch", e)
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            if (isPaused) {
                                imageProxy.close()
                                return@setAnalyzer
                            }

                            val now = SystemClock.elapsedRealtime()
                            // Power Saver Mode: throttle frame rate to max 8 fps (~125ms interval)
                            // Standard Mode: max 20 fps (~50ms interval) to avoid battery drain
                            val minIntervalMs = if (powerSaverMode) 125L else 50L
                            if (now - lastAnalyzedTime.get() < minIntervalMs || isProcessingFrame.get()) {
                                imageProxy.close()
                                return@setAnalyzer
                            }

                            @androidx.annotation.OptIn(ExperimentalGetImage::class)
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                isProcessingFrame.set(true)
                                lastAnalyzedTime.set(now)

                                val inputImage = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )

                                barcodeScanner.process(inputImage)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            val raw = barcode.rawValue
                                            if (!raw.isNullOrBlank()) {
                                                onBarcodeDetected(raw)
                                                break
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("CameraView", "ML Kit processing error", e)
                                    }
                                    .addOnCompleteListener {
                                        isProcessingFrame.set(false)
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }

                        val cameraSelector = when {
                            cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> CameraSelector.DEFAULT_BACK_CAMERA
                            cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.DEFAULT_FRONT_CAMERA
                            else -> null
                        }

                        if (cameraSelector == null) {
                            onCameraUnavailable("No physical or virtual camera detected on this device/emulator.")
                            return@addListener
                        }

                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                        cameraInstance = camera

                        if (camera.cameraInfo.hasFlashUnit() && isTorchEnabled && !isPaused) {
                            camera.cameraControl.enableTorch(true)
                        }
                    } catch (e: Exception) {
                        Log.e("CameraView", "Camera binding failed", e)
                        onCameraUnavailable(e.message ?: "Camera initialization failed")
                    }
                }, ContextCompat.getMainExecutor(ctx))
            } catch (e: Exception) {
                Log.e("CameraView", "ProcessCameraProvider failed", e)
                onCameraUnavailable(e.message ?: "Camera provider unavailable")
            }

            previewView
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            // Turn off torch when leaving camera screen to preserve battery
            try {
                cameraInstance?.let { cam ->
                    if (cam.cameraInfo.hasFlashUnit()) {
                        cam.cameraControl.enableTorch(false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }
}

/**
 * Decodes image picked from Gallery using ML Kit entirely on-device
 */
fun scanImageFromUri(
    context: Context,
    uri: Uri,
    onResult: (String?) -> Unit
) {
    try {
        val inputImage = InputImage.fromFilePath(context, uri)
        val scanner = BarcodeScanning.getClient()
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val detected = barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                onResult(detected)
            }
            .addOnFailureListener {
                onResult(null)
            }
    } catch (e: Exception) {
        e.printStackTrace()
        onResult(null)
    }
}
