package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DarkThemeConfig
import com.example.ui.components.AppResponsiveScaffold
import com.example.ui.components.OnboardingDialog
import com.example.ui.screens.AboutDeveloperScreen
import com.example.ui.screens.DesignerScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SamplesHubScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AlexQrTheme
import com.example.viewmodel.AppDestination
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val currentPalette by viewModel.selectedPalette.collectAsStateWithLifecycle()
            val darkThemeConfig by viewModel.darkThemeConfig.collectAsStateWithLifecycle()
            val currentDest by viewModel.currentDestination.collectAsStateWithLifecycle()
            val showOnboarding by viewModel.showOnboarding.collectAsStateWithLifecycle()

            val isDark = darkThemeConfig == DarkThemeConfig.DARK || darkThemeConfig == DarkThemeConfig.AMOLED

            AlexQrTheme(
                palette = currentPalette,
                darkThemeConfig = darkThemeConfig
            ) {
                AppResponsiveScaffold(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    currentDestination = currentDest,
                    isDarkTheme = isDark,
                    onNavigate = { dest -> viewModel.navigateTo(dest) },
                    onToggleTheme = {
                        val nextConfig = if (isDark) DarkThemeConfig.LIGHT else DarkThemeConfig.DARK
                        viewModel.setDarkThemeConfig(nextConfig)
                    },
                    onOpenHowTo = { viewModel.showOnboardingGuide() }
                ) {
                    when (currentDest) {
                        AppDestination.HOME -> HomeScreen(viewModel = viewModel)
                        AppDestination.SCANNER -> ScannerScreen(viewModel = viewModel)
                        AppDestination.DESIGNER -> DesignerScreen(viewModel = viewModel)
                        AppDestination.SAMPLES -> SamplesHubScreen(viewModel = viewModel)
                        AppDestination.HISTORY -> HistoryScreen(viewModel = viewModel)
                        AppDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        AppDestination.ABOUT_DEV -> AboutDeveloperScreen(viewModel = viewModel)
                    }
                }

                // First-time installation interactive guide
                if (showOnboarding) {
                    OnboardingDialog(
                        onDismiss = { dontShowAgain ->
                            viewModel.completeOnboarding(dontShowAgain)
                        }
                    )
                }
            }
        }
    }
}
