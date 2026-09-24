package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.DarkThemeConfig
import com.example.data.ThemePalette

fun buildDynamicColorScheme(palette: ThemePalette, isDark: Boolean, isAmoled: Boolean): ColorScheme {
    val primary = Color(palette.primaryColor)
    val secondary = Color(palette.secondaryColor)

    return if (isDark) {
        val bg = if (isAmoled) Color(0xFF000000) else Color(0xFF0F172A)
        val surf = if (isAmoled) Color(0xFF0A0A0A) else Color(0xFF1E293B)
        val surfVar = if (isAmoled) Color(0xFF141414) else Color(0xFF334155)

        darkColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.28f),
            onPrimaryContainer = Color(0xFFE0E7FF),
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.28f),
            onSecondaryContainer = Color(0xFFE2E8F0),
            tertiary = Color(0xFF38BDF8),
            onTertiary = Color(0xFF082F49),
            tertiaryContainer = Color(0xFF0369A1).copy(alpha = 0.35f),
            onTertiaryContainer = Color(0xFFBAE6FD),
            background = bg,
            onBackground = Color(0xFFF8FAFC),
            surface = surf,
            onSurface = Color(0xFFF1F5F9),
            surfaceVariant = surfVar,
            onSurfaceVariant = Color(0xFFCBD5E1)
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.12f),
            onPrimaryContainer = primary,
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.12f),
            onSecondaryContainer = secondary,
            tertiary = Color(0xFF0284C7),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFE0F2FE),
            onTertiaryContainer = Color(0xFF0369A1),
            background = Color(0xFFF8FAFC),
            onBackground = Color(0xFF0F172A),
            surface = Color.White,
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurfaceVariant = Color(0xFF475569)
        )
    }
}

@Composable
fun AlexQrTheme(
    palette: ThemePalette = ThemePalette.ELECTRIC_BLUE,
    darkThemeConfig: DarkThemeConfig = DarkThemeConfig.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (darkThemeConfig) {
        DarkThemeConfig.SYSTEM -> systemInDark
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
        DarkThemeConfig.AMOLED -> true
    }
    val isAmoled = darkThemeConfig == DarkThemeConfig.AMOLED

    val colorScheme = buildDynamicColorScheme(palette, isDark, isAmoled)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
