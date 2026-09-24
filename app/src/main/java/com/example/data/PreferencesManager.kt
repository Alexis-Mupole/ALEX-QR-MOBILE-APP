package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class ThemePalette(val displayName: String, val primaryColor: Long, val secondaryColor: Long) {
    ELECTRIC_BLUE("Electric Blue", 0xFF2563EB, 0xFF1D4ED8),
    EMERALD_GREEN("Emerald Green", 0xFF059669, 0xFF047857),
    ROYAL_PURPLE("Royal Purple", 0xFF7C3AED, 0xFF6D28D9),
    SUNSET_ORANGE("Sunset Orange", 0xFFEA580C, 0xFFC2410C),
    ROSE_PINK("Rose Pink", 0xFFE11D48, 0xFFBE123C),
    CYBER_CYAN("Cyber Cyan", 0xFF0891B2, 0xFF0E7490),
    DEEP_SLATE("Deep Slate", 0xFF334155, 0xFF1E293B)
}

enum class DarkThemeConfig {
    SYSTEM,
    LIGHT,
    DARK,
    AMOLED
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("alexqr_user_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PALETTE = "pref_theme_palette"
        private const val KEY_DARK_MODE = "pref_dark_mode"
        private const val KEY_POWER_SAVER = "pref_power_saver"
        private const val KEY_VIBRATE = "pref_vibrate_on_scan"
        private const val KEY_AUTO_COPY = "pref_auto_copy"
        private const val KEY_ONBOARDING_COMPLETED = "pref_onboarding_completed"
    }

    var selectedPalette: ThemePalette
        get() {
            val name = prefs.getString(KEY_PALETTE, ThemePalette.ELECTRIC_BLUE.name)
            return try {
                ThemePalette.valueOf(name ?: ThemePalette.ELECTRIC_BLUE.name)
            } catch (e: Exception) {
                ThemePalette.ELECTRIC_BLUE
            }
        }
        set(value) = prefs.edit().putString(KEY_PALETTE, value.name).apply()

    var darkThemeConfig: DarkThemeConfig
        get() {
            val name = prefs.getString(KEY_DARK_MODE, DarkThemeConfig.SYSTEM.name)
            return try {
                DarkThemeConfig.valueOf(name ?: DarkThemeConfig.SYSTEM.name)
            } catch (e: Exception) {
                DarkThemeConfig.SYSTEM
            }
        }
        set(value) = prefs.edit().putString(KEY_DARK_MODE, value.name).apply()

    var powerSaverMode: Boolean
        get() = prefs.getBoolean(KEY_POWER_SAVER, false)
        set(value) = prefs.edit().putBoolean(KEY_POWER_SAVER, value).apply()

    var vibrateOnScan: Boolean
        get() = prefs.getBoolean(KEY_VIBRATE, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATE, value).apply()

    var autoCopyOnScan: Boolean
        get() = prefs.getBoolean(KEY_AUTO_COPY, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_COPY, value).apply()

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()
}
