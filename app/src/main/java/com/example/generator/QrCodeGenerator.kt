package com.example.generator

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.CenterLogoType
import com.example.model.DotShape
import com.example.model.EyeStyle
import com.example.model.QrStyleConfig
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object QrCodeGenerator {

    /**
     * Generates a raw ZXing BitMatrix using the specified ErrorCorrectionLevel.
     * Defaults to ErrorCorrectionLevel.H (high error correction, ~30% recovery),
     * which allows embedding center brand logos while preserving 100% scanability.
     */
    fun generateBitMatrix(
        content: String,
        errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.H
    ): BitMatrix? {
        if (content.isBlank()) return null
        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to errorCorrection,
                EncodeHintType.MARGIN to 0
            )
            QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 0, 0, hints)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Computes raw boolean 2D matrix for the given QR content using ZXing BitMatrix.
     * Guaranteed to return exact QR version dimensions (e.g. 21x21, 25x25) with zero added padding,
     * ensuring finder patterns (eyes) are precisely anchored at (0,0), (0, size-7), and (size-7, 0).
     */
    fun generateMatrix(
        content: String,
        errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.H
    ): Array<BooleanArray>? {
        val bitMatrix = generateBitMatrix(content, errorCorrection) ?: return null
        val size = bitMatrix.width
        return Array(size) { row ->
            BooleanArray(size) { col ->
                bitMatrix.get(col, row)
            }
        }
    }

    /**
     * Checks if a cell at (row, col) is part of any of the 3 finder patterns (eyes).
     * Exact 7x7 boundaries per ISO/IEC 18004 specification.
     */
    fun isEyeModule(row: Int, col: Int, size: Int): Boolean {
        // Top-Left eye: [0..6, 0..6]
        if (row in 0..6 && col in 0..6) return true
        // Top-Right eye: [0..6, (size-7)..(size-1)]
        if (row in 0..6 && col in (size - 7) until size) return true
        // Bottom-Left eye: [(size-7)..(size-1), 0..6]
        if (row in (size - 7) until size && col in 0..6) return true
        return false
    }

    /**
     * Checks if a module is an essential structural element:
     * - Finder patterns (7x7 eyes)
     * - Timing patterns (Row 6 and Column 6)
     * - Format information bits (Row 8, Column 8)
     * Keeping these square ensures 100% reliable hardware & software scanability.
     */
    fun isStructuralModule(row: Int, col: Int, size: Int): Boolean {
        if (isEyeModule(row, col, size)) return true
        // Timing patterns
        if (row == 6 || col == 6) return true
        // Format information
        if ((row == 8 && (col in 0..8 || col >= size - 8)) ||
            (col == 8 && (row in 0..8 || row >= size - 8))) return true
        return false
    }

    /**
     * Renders high-resolution Android Bitmap with standard 4-module quiet zone margin,
     * integer pixel alignment, anti-aliased geometry, center logo embedding, and optional
     * bottom call-to-action text (e.g., "SCAN ME").
     */
    fun renderStyledBitmap(
        matrix: Array<BooleanArray>,
        style: QrStyleConfig,
        dimensionPx: Int = 1024,
        customLogoBitmap: Bitmap? = null
    ): Bitmap {
        val qrSize = matrix.size
        // Standard ISO/IEC 18004 quiet zone: 4 modules
        val quietZone = 4
        val totalModules = qrSize + quietZone * 2
        val scale = maxOf(1, dimensionPx / totalModules)
        val qrDimension = totalModules * scale
        val offset = quietZone * scale

        // Calculate extra vertical space for bottom text banner if enabled
        val showBottom = style.showBottomText && style.bottomText.isNotBlank()
        val textSectionHeight = if (showBottom) (qrDimension * 0.16f).toInt() else 0
        val totalBitmapHeight = qrDimension + textSectionHeight

        val bitmap = Bitmap.createBitmap(qrDimension, totalBitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Solid Background with high contrast
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.backgroundColor.toInt()
            this.style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, qrDimension.toFloat(), totalBitmapHeight.toFloat(), bgPaint)

        // 2. Setup Foreground Shader/Paint with anti-aliasing
        val fgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
            this.style = Paint.Style.FILL
            if (style.useGradient) {
                shader = LinearGradient(
                    offset.toFloat(), offset.toFloat(),
                    (offset + qrSize * scale).toFloat(), (offset + qrSize * scale).toFloat(),
                    style.primaryColor.toInt(),
                    style.secondaryColor.toInt(),
                    Shader.TileMode.CLAMP
                )
            } else {
                color = style.primaryColor.toInt()
            }
        }

        // 3. Draw Body Modules & Structural Patterns
        for (r in 0 until qrSize) {
            for (c in 0 until qrSize) {
                if (!matrix[r][c]) continue
                val isStructural = isStructuralModule(r, c, qrSize)

                val left = (offset + c * scale).toFloat()
                val top = (offset + r * scale).toFloat()
                val right = left + scale
                val bottom = top + scale

                if (isStructural || style.dotShape == DotShape.SQUARE) {
                    canvas.drawRect(left, top, right, bottom, fgPaint)
                } else {
                    when (style.dotShape) {
                        DotShape.ROUNDED -> {
                            val cornerRadius = scale * 0.30f
                            canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, fgPaint)
                        }
                        DotShape.CIRCLES -> {
                            val cx = left + (scale / 2f)
                            val cy = top + (scale / 2f)
                            val radius = (scale / 2f) * 0.95f
                            canvas.drawCircle(cx, cy, radius, fgPaint)
                        }
                        DotShape.SQUARE -> {
                            canvas.drawRect(left, top, right, bottom, fgPaint)
                        }
                    }
                }
            }
        }

        // 4. If custom eye styling is requested, apply to 7x7 outer patterns
        if (style.eyeStyle != EyeStyle.SQUARE) {
            val eyeCoordinates = listOf(
                Pair(0, 0),
                Pair(0, qrSize - 7),
                Pair(qrSize - 7, 0)
            )

            for ((er, ec) in eyeCoordinates) {
                val startX = (offset + ec * scale).toFloat()
                val startY = (offset + er * scale).toFloat()
                val eyeSize = 7f * scale
                val spacerOffset = 1f * scale
                val spacerSize = 5f * scale
                val pupilOffset = 2f * scale
                val pupilSize = 3f * scale

                // Clear 7x7 eye box to background first
                canvas.drawRect(startX, startY, startX + eyeSize, startY + eyeSize, bgPaint)

                if (style.eyeStyle == EyeStyle.ROUNDED) {
                    val outerCr = scale * 1.5f
                    val spacerCr = scale * 1.0f
                    val pupilCr = scale * 0.6f
                    canvas.drawRoundRect(RectF(startX, startY, startX + eyeSize, startY + eyeSize), outerCr, outerCr, fgPaint)
                    canvas.drawRoundRect(RectF(startX + spacerOffset, startY + spacerOffset, startX + spacerOffset + spacerSize, startY + spacerOffset + spacerSize), spacerCr, spacerCr, bgPaint)
                    canvas.drawRoundRect(RectF(startX + pupilOffset, startY + pupilOffset, startX + pupilOffset + pupilSize, startY + pupilOffset + pupilSize), pupilCr, pupilCr, fgPaint)
                } else if (style.eyeStyle == EyeStyle.CIRCLES) {
                    val cx = startX + (eyeSize / 2f)
                    val cy = startY + (eyeSize / 2f)
                    canvas.drawCircle(cx, cy, eyeSize / 2f, fgPaint)
                    canvas.drawCircle(cx, cy, spacerSize / 2f, bgPaint)
                    canvas.drawCircle(cx, cy, pupilSize / 2f, fgPaint)
                }
            }
        }

        // 5. Draw Center Brand Logo (if enabled)
        if (style.centerLogo != CenterLogoType.NONE) {
            drawCenterLogo(
                canvas = canvas,
                centerLogo = style.centerLogo,
                customBitmap = customLogoBitmap,
                centerX = qrDimension / 2f,
                centerY = qrDimension / 2f,
                badgeSize = qrDimension * 0.22f,
                bgColor = style.backgroundColor.toInt(),
                primaryColor = style.primaryColor.toInt(),
                secondaryColor = style.secondaryColor.toInt()
            )
        }

        // 6. Draw Bottom Banner / Call-to-Action Text (e.g. "SCAN ME")
        if (showBottom) {
            drawBottomTextBanner(
                canvas = canvas,
                text = style.bottomText,
                qrWidth = qrDimension.toFloat(),
                textSectionTop = qrDimension.toFloat(),
                textSectionHeight = textSectionHeight.toFloat(),
                primaryColor = style.primaryColor.toInt(),
                secondaryColor = style.secondaryColor.toInt(),
                bgColor = style.backgroundColor.toInt(),
                useGradient = style.useGradient
            )
        }

        return bitmap
    }

    /**
     * Draws a clean, anti-aliased center logo badge in the middle of the QR code.
     */
    private fun drawCenterLogo(
        canvas: Canvas,
        centerLogo: CenterLogoType,
        customBitmap: Bitmap?,
        centerX: Float,
        centerY: Float,
        badgeSize: Float,
        bgColor: Int,
        primaryColor: Int,
        secondaryColor: Int
    ) {
        val halfBadge = badgeSize / 2f
        val badgeRect = RectF(centerX - halfBadge, centerY - halfBadge, centerX + halfBadge, centerY + halfBadge)
        val cornerRadius = badgeSize * 0.25f

        // Draw solid background badge
        val badgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            this.style = Paint.Style.FILL
        }
        canvas.drawRoundRect(badgeRect, cornerRadius, cornerRadius, badgeBgPaint)

        // Draw badge outline border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            this.style = Paint.Style.STROKE
            strokeWidth = maxOf(2f, badgeSize * 0.04f)
        }
        canvas.drawRoundRect(badgeRect, cornerRadius, cornerRadius, borderPaint)

        // Draw icon / custom bitmap inside badge
        val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            color = primaryColor
            this.style = Paint.Style.FILL
        }

        if (centerLogo == CenterLogoType.CUSTOM && customBitmap != null) {
            val contentSize = badgeSize * 0.72f
            val iconRect = RectF(
                centerX - (contentSize / 2f),
                centerY - (contentSize / 2f),
                centerX + (contentSize / 2f),
                centerY + (contentSize / 2f)
            )
            canvas.drawBitmap(customBitmap, null, iconRect, iconPaint)
        } else {
            drawPresetVectorIcon(canvas, centerLogo, centerX, centerY, badgeSize * 0.58f, iconPaint)
        }
    }

    /**
     * Draws crisp, resolution-independent vector glyphs for preset center logos.
     */
    private fun drawPresetVectorIcon(
        canvas: Canvas,
        logo: CenterLogoType,
        cx: Float,
        cy: Float,
        size: Float,
        paint: Paint
    ) {
        val half = size / 2f
        when (logo) {
            CenterLogoType.ALEX_QR -> {
                // Stylized Mini QR icon
                val unit = size / 5f
                val left = cx - half
                val top = cy - half
                // Outer corners
                canvas.drawRect(left, top, left + unit * 2, top + unit * 2, paint)
                canvas.drawRect(left + unit * 3, top, left + size, top + unit * 2, paint)
                canvas.drawRect(left, top + unit * 3, left + unit * 2, top + size, paint)
                // Center bit
                canvas.drawRect(left + unit * 2, top + unit * 2, left + unit * 3, top + unit * 3, paint)
            }
            CenterLogoType.LINK -> {
                // Interlocked chain link
                val r = size * 0.35f
                val stroke = Paint(paint).apply {
                    this.style = Paint.Style.STROKE
                    strokeWidth = size * 0.16f
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawCircle(cx - (size * 0.18f), cy - (size * 0.12f), r * 0.6f, stroke)
                canvas.drawCircle(cx + (size * 0.18f), cy + (size * 0.12f), r * 0.6f, stroke)
            }
            CenterLogoType.WIFI -> {
                // Wi-Fi signal arcs
                val arcPaint = Paint(paint).apply {
                    this.style = Paint.Style.STROKE
                    strokeWidth = size * 0.14f
                    strokeCap = Paint.Cap.ROUND
                }
                // Center dot
                canvas.drawCircle(cx, cy + (size * 0.28f), size * 0.10f, paint)
                // Inner arc
                val innerRect = RectF(cx - (size * 0.28f), cy - (size * 0.05f), cx + (size * 0.28f), cy + (size * 0.50f))
                canvas.drawArc(innerRect, 200f, 140f, false, arcPaint)
                // Outer arc
                val outerRect = RectF(cx - (size * 0.48f), cy - (size * 0.25f), cx + (size * 0.48f), cy + (size * 0.70f))
                canvas.drawArc(outerRect, 200f, 140f, false, arcPaint)
            }
            CenterLogoType.HEART -> {
                val path = Path().apply {
                    moveTo(cx, cy + (half * 0.7f))
                    cubicTo(cx - half, cy - (half * 0.2f), cx - half, cy - half, cx - (half * 0.4f), cy - half)
                    cubicTo(cx - (half * 0.1f), cy - half, cx, cy - (half * 0.5f), cx, cy - (half * 0.3f))
                    cubicTo(cx, cy - (half * 0.5f), cx + (half * 0.1f), cy - half, cx + (half * 0.4f), cy - half)
                    cubicTo(cx + half, cy - half, cx + half, cy - (half * 0.2f), cx, cy + (half * 0.7f))
                    close()
                }
                canvas.drawPath(path, paint)
            }
            CenterLogoType.STAR -> {
                val path = Path()
                val numPoints = 5
                val outerRadius = half
                val innerRadius = half * 0.45f
                for (i in 0 until numPoints * 2) {
                    val radius = if (i % 2 == 0) outerRadius else innerRadius
                    val angle = (i * Math.PI / numPoints) - (Math.PI / 2)
                    val x = (cx + radius * Math.cos(angle)).toFloat()
                    val y = (cy + radius * Math.sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                canvas.drawPath(path, paint)
            }
            CenterLogoType.USER -> {
                // Head
                canvas.drawCircle(cx, cy - (half * 0.35f), half * 0.40f, paint)
                // Body arc
                val bodyRect = RectF(cx - (half * 0.85f), cy + (half * 0.05f), cx + (half * 0.85f), cy + (half * 1.5f))
                canvas.drawArc(bodyRect, 190f, 160f, true, paint)
            }
            else -> {}
        }
    }

    /**
     * Draws an anti-aliased pill-shaped banner with custom call-to-action text (e.g. "SCAN ME")
     * below the QR code.
     */
    private fun drawBottomTextBanner(
        canvas: Canvas,
        text: String,
        qrWidth: Float,
        textSectionTop: Float,
        textSectionHeight: Float,
        primaryColor: Int,
        secondaryColor: Int,
        bgColor: Int,
        useGradient: Boolean
    ) {
        val bannerHeight = textSectionHeight * 0.65f
        val bannerWidth = (qrWidth * 0.68f).coerceAtLeast(200f)
        val bannerLeft = (qrWidth - bannerWidth) / 2f
        val bannerTop = textSectionTop + ((textSectionHeight - bannerHeight) / 2f)
        val bannerRect = RectF(bannerLeft, bannerTop, bannerLeft + bannerWidth, bannerTop + bannerHeight)
        val bannerRadius = bannerHeight / 2f

        // Pill background
        val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.style = Paint.Style.FILL
            if (useGradient) {
                shader = LinearGradient(
                    bannerLeft, bannerTop,
                    bannerLeft + bannerWidth, bannerTop + bannerHeight,
                    primaryColor, secondaryColor,
                    Shader.TileMode.CLAMP
                )
            } else {
                color = primaryColor
            }
        }
        canvas.drawRoundRect(bannerRect, bannerRadius, bannerRadius, pillPaint)

        // Text inside banner
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = bannerHeight * 0.48f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        // Vertical centering font metric
        val fontMetrics = textPaint.fontMetrics
        val textY = bannerTop + (bannerHeight / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)
        canvas.drawText(text.uppercase(), qrWidth / 2f, textY, textPaint)
    }

    /**
     * Safely decodes a bitmap from a content Uri (e.g., from PhotoPicker).
     */
    fun decodeBitmapFromUri(context: Context, uriString: String?, maxDim: Int = 256): Bitmap? {
        if (uriString.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(uriString)
            val input: InputStream? = context.contentResolver.openInputStream(uri)
            val original = BitmapFactory.decodeStream(input)
            input?.close()
            if (original != null && (original.width > maxDim || original.height > maxDim)) {
                val scale = maxDim.toFloat() / maxOf(original.width, original.height)
                Bitmap.createScaledBitmap(
                    original,
                    (original.width * scale).toInt(),
                    (original.height * scale).toInt(),
                    true
                )
            } else {
                original
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves the QR Bitmap into public MediaStore (Pictures/AlexQr) entirely offline.
     */
    fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap, title: String): Uri? {
        val safeName = title.filter { it.isLetterOrDigit() || it == '_' }.take(30).ifBlank { "QR" }
        val filename = "AlexQr_${safeName}_${System.currentTimeMillis()}.png"
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/AlexQr")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return null

        try {
            resolver.openOutputStream(imageUri)?.use { out: OutputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
            return imageUri
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Caches the bitmap into internal cache and shares via Android Intent with FileProvider.
     */
    fun shareQrCode(context: Context, bitmap: Bitmap, contentText: String) {
        try {
            val cacheDir = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(cacheDir, "shared_qr_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val authority = "${context.packageName}.fileprovider"
            val contentUri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "Scannable QR Code:\n$contentText")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share QR Code via..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
