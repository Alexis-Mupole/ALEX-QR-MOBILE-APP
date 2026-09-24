package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generator.QrCodeGenerator
import com.example.model.CenterLogoType
import com.example.model.DotShape
import com.example.model.EyeStyle
import com.example.model.QrStyleConfig

@Composable
fun StyledQrCanvas(
    matrix: Array<BooleanArray>?,
    style: QrStyleConfig,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backgroundColor = remember(style.backgroundColor) { Color(style.backgroundColor) }
    val primaryColor = remember(style.primaryColor) { Color(style.primaryColor) }
    val secondaryColor = remember(style.secondaryColor) { Color(style.secondaryColor) }

    // Resolve custom logo bitmap if custom logo is chosen
    val customBitmap: Bitmap? = remember(style.centerLogo, style.customLogoUri) {
        if (style.centerLogo == CenterLogoType.CUSTOM && style.customLogoUri != null) {
            QrCodeGenerator.decodeBitmapFromUri(context, style.customLogoUri, maxDim = 256)
        } else {
            null
        }
    }

    val showBottom = style.showBottomText && style.bottomText.isNotBlank()

    Card(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // QR Code Matrix Box (1:1 aspect ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                if (matrix != null && matrix.isNotEmpty()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val matrixSize = matrix.size
                        val canvasSize = size.minDimension

                        // Standard quiet zone margin of 3.5 modules
                        val quietZoneModules = 3.5f
                        val totalModules = matrixSize + (quietZoneModules * 2f)
                        val moduleSize = canvasSize / totalModules
                        val offset = quietZoneModules * moduleSize

                        val brush = if (style.useGradient) {
                            Brush.linearGradient(
                                colors = listOf(primaryColor, secondaryColor),
                                start = Offset(offset, offset),
                                end = Offset(offset + (matrixSize * moduleSize), offset + (matrixSize * moduleSize))
                            )
                        } else {
                            SolidColor(primaryColor)
                        }

                        val bgBrush = SolidColor(backgroundColor)

                        // 1. Draw Data & Timing Modules (excluding finder pattern eye zones)
                        for (r in 0 until matrixSize) {
                            for (c in 0 until matrixSize) {
                                if (!matrix[r][c]) continue
                                if (QrCodeGenerator.isEyeModule(r, c, matrixSize)) continue

                                val left = offset + (c * moduleSize)
                                val top = offset + (r * moduleSize)

                                if (QrCodeGenerator.isStructuralModule(r, c, matrixSize) || style.dotShape == DotShape.SQUARE) {
                                    drawRect(
                                        brush = brush,
                                        topLeft = Offset(left, top),
                                        size = Size(moduleSize, moduleSize)
                                    )
                                } else {
                                    when (style.dotShape) {
                                        DotShape.ROUNDED -> {
                                            val cr = moduleSize * 0.32f
                                            drawRoundRect(
                                                brush = brush,
                                                topLeft = Offset(left, top),
                                                size = Size(moduleSize, moduleSize),
                                                cornerRadius = CornerRadius(cr, cr)
                                            )
                                        }
                                        DotShape.CIRCLES -> {
                                            val cx = left + (moduleSize / 2f)
                                            val cy = top + (moduleSize / 2f)
                                            val radius = (moduleSize / 2f) * 0.95f
                                            drawCircle(
                                                brush = brush,
                                                center = Offset(cx, cy),
                                                radius = radius
                                            )
                                        }
                                        DotShape.SQUARE -> {
                                            drawRect(
                                                brush = brush,
                                                topLeft = Offset(left, top),
                                                size = Size(moduleSize, moduleSize)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Draw 7x7 Finder Pattern Eyes
                        val eyeCoordinates = listOf(
                            Pair(0, 0),
                            Pair(0, matrixSize - 7),
                            Pair(matrixSize - 7, 0)
                        )

                        for ((r, c) in eyeCoordinates) {
                            drawComposeEye(
                                startX = offset + (c * moduleSize),
                                startY = offset + (r * moduleSize),
                                moduleSize = moduleSize,
                                style = style,
                                fgBrush = brush,
                                bgBrush = bgBrush
                            )
                        }

                        // 3. Draw Center Brand Logo if enabled
                        if (style.centerLogo != CenterLogoType.NONE) {
                            val qrAreaCenter = canvasSize / 2f
                            val badgeSize = canvasSize * 0.22f
                            drawComposeCenterLogo(
                                cx = qrAreaCenter,
                                cy = qrAreaCenter,
                                badgeSize = badgeSize,
                                logo = style.centerLogo,
                                customBitmap = customBitmap,
                                bgColor = backgroundColor,
                                fgBrush = brush,
                                primaryColor = primaryColor
                            )
                        }
                    }
                }
            }

            // Bottom Call-To-Action Pill Banner (e.g. "SCAN ME")
            if (showBottom) {
                Spacer(modifier = Modifier.height(8.dp))
                val pillBrush = if (style.useGradient) {
                    Brush.horizontalGradient(listOf(primaryColor, secondaryColor))
                } else {
                    SolidColor(primaryColor)
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Transparent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(pillBrush)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = style.bottomText.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawComposeEye(
    startX: Float,
    startY: Float,
    moduleSize: Float,
    style: QrStyleConfig,
    fgBrush: Brush,
    bgBrush: Brush
) {
    val outerSize = 7 * moduleSize
    val spacerOffset = 1 * moduleSize
    val spacerSize = 5 * moduleSize
    val pupilOffset = 2 * moduleSize
    val pupilSize = 3 * moduleSize

    when (style.eyeStyle) {
        EyeStyle.SQUARE -> {
            drawRect(brush = fgBrush, topLeft = Offset(startX, startY), size = Size(outerSize, outerSize))
            drawRect(brush = bgBrush, topLeft = Offset(startX + spacerOffset, startY + spacerOffset), size = Size(spacerSize, spacerSize))
            drawRect(brush = fgBrush, topLeft = Offset(startX + pupilOffset, startY + pupilOffset), size = Size(pupilSize, pupilSize))
        }
        EyeStyle.ROUNDED -> {
            val outerCr = moduleSize * 1.5f
            val spacerCr = moduleSize * 1.0f
            val pupilCr = moduleSize * 0.6f

            drawRoundRect(brush = fgBrush, topLeft = Offset(startX, startY), size = Size(outerSize, outerSize), cornerRadius = CornerRadius(outerCr, outerCr))
            drawRoundRect(brush = bgBrush, topLeft = Offset(startX + spacerOffset, startY + spacerOffset), size = Size(spacerSize, spacerSize), cornerRadius = CornerRadius(spacerCr, spacerCr))
            drawRoundRect(brush = fgBrush, topLeft = Offset(startX + pupilOffset, startY + pupilOffset), size = Size(pupilSize, pupilSize), cornerRadius = CornerRadius(pupilCr, pupilCr))
        }
        EyeStyle.CIRCLES -> {
            val cx = startX + (outerSize / 2f)
            val cy = startY + (outerSize / 2f)

            drawCircle(brush = fgBrush, center = Offset(cx, cy), radius = outerSize / 2f)
            drawCircle(brush = bgBrush, center = Offset(cx, cy), radius = spacerSize / 2f)
            drawCircle(brush = fgBrush, center = Offset(cx, cy), radius = pupilSize / 2f)
        }
    }
}

private fun DrawScope.drawComposeCenterLogo(
    cx: Float,
    cy: Float,
    badgeSize: Float,
    logo: CenterLogoType,
    customBitmap: Bitmap?,
    bgColor: Color,
    fgBrush: Brush,
    primaryColor: Color
) {
    val halfBadge = badgeSize / 2f
    val cornerRadius = badgeSize * 0.25f

    // 1. Badge Background
    drawRoundRect(
        color = bgColor,
        topLeft = Offset(cx - halfBadge, cy - halfBadge),
        size = Size(badgeSize, badgeSize),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )

    // 2. Badge Border
    drawRoundRect(
        brush = fgBrush,
        topLeft = Offset(cx - halfBadge, cy - halfBadge),
        size = Size(badgeSize, badgeSize),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius),
        style = Stroke(width = maxOf(2f, badgeSize * 0.05f))
    )

    // 3. Logo Icon or Bitmap
    val iconSize = badgeSize * 0.58f
    val halfIcon = iconSize / 2f

    if (logo == CenterLogoType.CUSTOM && customBitmap != null) {
        val imageBitmap = customBitmap.asImageBitmap()
        drawImage(
            image = imageBitmap,
            dstOffset = IntOffset((cx - halfIcon).toInt(), (cy - halfIcon).toInt()),
            dstSize = IntSize(iconSize.toInt(), iconSize.toInt())
        )
    } else {
        when (logo) {
            CenterLogoType.ALEX_QR -> {
                val unit = iconSize / 5f
                val left = cx - halfIcon
                val top = cy - halfIcon
                drawRect(fgBrush, topLeft = Offset(left, top), size = Size(unit * 2, unit * 2))
                drawRect(fgBrush, topLeft = Offset(left + unit * 3, top), size = Size(unit * 2, unit * 2))
                drawRect(fgBrush, topLeft = Offset(left, top + unit * 3), size = Size(unit * 2, unit * 2))
                drawRect(fgBrush, topLeft = Offset(left + unit * 2, top + unit * 2), size = Size(unit, unit))
            }
            CenterLogoType.LINK -> {
                val r = iconSize * 0.22f
                val stroke = Stroke(width = iconSize * 0.16f, cap = StrokeCap.Round)
                drawCircle(fgBrush, center = Offset(cx - (iconSize * 0.16f), cy - (iconSize * 0.12f)), radius = r, style = stroke)
                drawCircle(fgBrush, center = Offset(cx + (iconSize * 0.16f), cy + (iconSize * 0.12f)), radius = r, style = stroke)
            }
            CenterLogoType.WIFI -> {
                val stroke = Stroke(width = iconSize * 0.14f, cap = StrokeCap.Round)
                drawCircle(fgBrush, center = Offset(cx, cy + (iconSize * 0.26f)), radius = iconSize * 0.09f)
                drawArc(
                    brush = fgBrush,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - (iconSize * 0.28f), cy - (iconSize * 0.05f)),
                    size = Size(iconSize * 0.56f, iconSize * 0.55f),
                    style = stroke
                )
                drawArc(
                    brush = fgBrush,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - (iconSize * 0.48f), cy - (iconSize * 0.25f)),
                    size = Size(iconSize * 0.96f, iconSize * 0.95f),
                    style = stroke
                )
            }
            CenterLogoType.HEART -> {
                val path = Path().apply {
                    moveTo(cx, cy + (halfIcon * 0.7f))
                    cubicTo(cx - halfIcon, cy - (halfIcon * 0.2f), cx - halfIcon, cy - halfIcon, cx - (halfIcon * 0.4f), cy - halfIcon)
                    cubicTo(cx - (halfIcon * 0.1f), cy - halfIcon, cx, cy - (halfIcon * 0.5f), cx, cy - (halfIcon * 0.3f))
                    cubicTo(cx, cy - (halfIcon * 0.5f), cx + (halfIcon * 0.1f), cy - halfIcon, cx + (halfIcon * 0.4f), cy - halfIcon)
                    cubicTo(cx + halfIcon, cy - halfIcon, cx + halfIcon, cy - (halfIcon * 0.2f), cx, cy + (halfIcon * 0.7f))
                    close()
                }
                drawPath(path, fgBrush)
            }
            CenterLogoType.STAR -> {
                val path = Path()
                val numPoints = 5
                val outerRadius = halfIcon
                val innerRadius = halfIcon * 0.45f
                for (i in 0 until numPoints * 2) {
                    val radius = if (i % 2 == 0) outerRadius else innerRadius
                    val angle = (i * Math.PI / numPoints) - (Math.PI / 2)
                    val x = (cx + radius * Math.cos(angle)).toFloat()
                    val y = (cy + radius * Math.sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, fgBrush)
            }
            CenterLogoType.USER -> {
                drawCircle(fgBrush, center = Offset(cx, cy - (halfIcon * 0.35f)), radius = halfIcon * 0.40f)
                drawArc(
                    brush = fgBrush,
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = true,
                    topLeft = Offset(cx - (halfIcon * 0.85f), cy + (halfIcon * 0.05f)),
                    size = Size(halfIcon * 1.7f, halfIcon * 1.45f)
                )
            }
            else -> {}
        }
    }
}
