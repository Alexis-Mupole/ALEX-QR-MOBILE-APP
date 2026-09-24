package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun LaserScanOverlay(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_position"
    )

    val laserGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_glow"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Calculate responsive square scan window (e.g. 72% of minimum dimension, max 320dp)
            val boxSize = min(width, height) * 0.72f
            val left = (width - boxSize) / 2f
            val top = (height - boxSize) / 2f
            val right = left + boxSize
            val bottom = top + boxSize
            val cornerRadius = 24.dp.toPx()

            // 1. Draw Darkened Scrim Outside Viewport
            val viewportPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(left, top, right, bottom),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                )
            }

            clipPath(viewportPath, clipOp = ClipOp.Difference) {
                drawRect(
                    color = Color.Black.copy(alpha = 0.55f),
                    size = size
                )
            }

            // 2. Draw Sleek Viewport Border
            drawRoundRect(
                color = Color.White.copy(alpha = 0.25f),
                topLeft = Offset(left, top),
                size = Size(boxSize, boxSize),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )

            // 3. Draw High-Tech Corner Reticle Brackets
            val bracketLength = boxSize * 0.16f
            val bracketStroke = 4.5.dp.toPx()
            val bracketColor = primaryColor

            // Top-Left bracket
            drawLine(
                color = bracketColor,
                start = Offset(left + cornerRadius * 0.4f, top),
                end = Offset(left + bracketLength, top),
                strokeWidth = bracketStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = bracketColor,
                start = Offset(left, top + cornerRadius * 0.4f),
                end = Offset(left, top + bracketLength),
                strokeWidth = bracketStroke,
                cap = StrokeCap.Round
            )

            // Top-Right bracket
            drawLine(
                color = bracketColor,
                start = Offset(right - cornerRadius * 0.4f, top),
                end = Offset(right - bracketLength, top),
                strokeWidth = bracketStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = bracketColor,
                start = Offset(right, top + cornerRadius * 0.4f),
                end = Offset(right, top + bracketLength),
                strokeWidth = bracketStroke,
                cap = StrokeCap.Round
            )

            // Bottom-Left bracket
            drawLine(
                color = bracketColor,
                start = Offset(left + cornerRadius * 0.4f, bottom),
                end = Offset(left + bracketLength, bottom),
                strokeWidth = bracketStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = bracketColor,
                start = Offset(left, bottom - cornerRadius * 0.4f),
                end = Offset(left, bottom - bracketLength),
                strokeWidth = bracketStroke,
                cap = StrokeCap.Round
            )

            // Bottom-Right bracket
            drawLine(
                color = bracketColor,
                start = Offset(right - cornerRadius * 0.4f, bottom),
                end = Offset(right - bracketLength, bottom),
                strokeWidth = bracketStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = bracketColor,
                start = Offset(right, bottom - cornerRadius * 0.4f),
                end = Offset(right, bottom - bracketLength),
                strokeWidth = bracketStroke,
                cap = StrokeCap.Round
            )

            // 4. Draw Animated Laser Line with Soft Halo Glow
            if (isScanning) {
                clipPath(viewportPath) {
                    val currentLaserY = top + (boxSize * laserPosition)
                    val laserHeight = 3.dp.toPx()
                    val haloHeight = 28.dp.toPx()

                    // Glow gradient plume
                    val glowBrush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0f),
                            primaryColor.copy(alpha = 0.28f * laserGlowAlpha),
                            primaryColor.copy(alpha = 0.02f)
                        ),
                        startY = currentLaserY - haloHeight,
                        endY = currentLaserY + haloHeight
                    )
                    drawRect(
                        brush = glowBrush,
                        topLeft = Offset(left, currentLaserY - haloHeight),
                        size = Size(boxSize, haloHeight * 2)
                    )

                    // Sharp Laser Center Beam
                    val beamBrush = Brush.horizontalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.1f),
                            primaryColor.copy(alpha = 0.95f),
                            secondaryColor.copy(alpha = 0.95f),
                            primaryColor.copy(alpha = 0.1f)
                        ),
                        startX = left,
                        endX = right
                    )
                    drawRoundRect(
                        brush = beamBrush,
                        topLeft = Offset(left + 8.dp.toPx(), currentLaserY - (laserHeight / 2f)),
                        size = Size(boxSize - 16.dp.toPx(), laserHeight),
                        cornerRadius = CornerRadius(laserHeight / 2f, laserHeight / 2f)
                    )
                }
            }
        }
    }
}
