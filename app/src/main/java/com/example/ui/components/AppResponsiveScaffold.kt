package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppDestination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppResponsiveScaffold(
    windowWidthSizeClass: WindowWidthSizeClass,
    currentDestination: AppDestination,
    isDarkTheme: Boolean,
    onNavigate: (AppDestination) -> Unit,
    onToggleTheme: () -> Unit,
    onOpenHowTo: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isWideScreen = windowWidthSizeClass == WindowWidthSizeClass.Expanded ||
            windowWidthSizeClass == WindowWidthSizeClass.Medium

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 18.dp)
                ) {
                    // Drawer Header with Personalization callout
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = "Logo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "AlexQr Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Production Offline Suite",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Personalize CTA Banner in Drawer
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                scope.launch { drawerState.close() }
                                onNavigate(AppDestination.SETTINGS)
                            },
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Personalize App & Colors",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Themes, colors & battery saver",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "CORE FEATURES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        label = { Text("Home & Dashboard") },
                        selected = currentDestination == AppDestination.HOME,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(AppDestination.HOME)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        label = { Text("Offline Camera Scanner") },
                        selected = currentDestination == AppDestination.SCANNER,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(AppDestination.SCANNER)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        label = { Text("QR Studio & Brand Logos") },
                        selected = currentDestination == AppDestination.DESIGNER,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(AppDestination.DESIGNER)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        label = { Text("Preset Sample Hub") },
                        selected = currentDestination == AppDestination.SAMPLES,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(AppDestination.SAMPLES)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        label = { Text("Saved History Library") },
                        selected = currentDestination == AppDestination.HISTORY,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(AppDestination.HISTORY)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "PREFERENCES & CREATOR",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        label = { Text("Settings & Personalization") },
                        selected = currentDestination == AppDestination.SETTINGS,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(AppDestination.SETTINGS)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        label = { Text("About the Developer") },
                        selected = currentDestination == AppDestination.ABOUT_DEV,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(AppDestination.ABOUT_DEV)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        label = { Text("How to Use AlexQr") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onOpenHowTo()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Drawer Footer
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "AlexQr Pro v1.0.0",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Engineered by MUPOLE UWIZEYE Alexis",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) {
        if (isWideScreen) {
            // Wide Screen: Navigation Rail Layout
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier
                        .fillMaxHeight()
                        .testTag("app_navigation_rail"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                        ) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("hamburger_menu_button_rail")
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Open Menu")
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.QrCode,
                                    contentDescription = "AlexQr",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.HOME,
                        onClick = { onNavigate(AppDestination.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(20.dp)) },
                        label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("nav_rail_home")
                    )

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.SCANNER,
                        onClick = { onNavigate(AppDestination.SCANNER) },
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", modifier = Modifier.size(20.dp)) },
                        label = { Text("Scan", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("nav_rail_scan")
                    )

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.DESIGNER,
                        onClick = { onNavigate(AppDestination.DESIGNER) },
                        icon = { Icon(Icons.Default.Palette, contentDescription = "Designer", modifier = Modifier.size(20.dp)) },
                        label = { Text("Design", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("nav_rail_designer")
                    )

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.SAMPLES,
                        onClick = { onNavigate(AppDestination.SAMPLES) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Samples", modifier = Modifier.size(20.dp)) },
                        label = { Text("Samples", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("nav_rail_samples")
                    )

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.HISTORY,
                        onClick = { onNavigate(AppDestination.HISTORY) },
                        icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(20.dp)) },
                        label = { Text("History", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("nav_rail_history")
                    )

                    NavigationRailItem(
                        selected = currentDestination == AppDestination.SETTINGS,
                        onClick = { onNavigate(AppDestination.SETTINGS) },
                        icon = { Icon(Icons.Default.Tune, contentDescription = "Settings", modifier = Modifier.size(20.dp)) },
                        label = { Text("Settings", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("nav_rail_settings")
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("hamburger_menu_button")
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "Open Drawer Menu")
                                }
                            },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = getDestinationTitle(currentDestination),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    OfflineBadge()
                                }
                            },
                            actions = {
                                IconButton(onClick = { onNavigate(AppDestination.SETTINGS) }) {
                                    Icon(Icons.Default.Tune, contentDescription = "Settings", modifier = Modifier.size(20.dp))
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        content()
                    }
                }
            }
        } else {
            // Compact Phone: Top App Bar with Hamburger Icon + Bottom Navigation Bar
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("hamburger_menu_button")
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Open Drawer Menu")
                            }
                        },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = getDestinationTitle(currentDestination),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OfflineBadge()
                            }
                        },
                        actions = {
                            IconButton(onClick = { onNavigate(AppDestination.SETTINGS) }) {
                                Icon(Icons.Default.Tune, contentDescription = "Settings", modifier = Modifier.size(20.dp))
                            }
                            IconButton(
                                onClick = onToggleTheme,
                                modifier = Modifier.testTag("theme_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Toggle Theme",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("app_bottom_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentDestination == AppDestination.HOME,
                            onClick = { onNavigate(AppDestination.HOME) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(20.dp)) },
                            label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_bottom_home")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.SCANNER,
                            onClick = { onNavigate(AppDestination.SCANNER) },
                            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", modifier = Modifier.size(20.dp)) },
                            label = { Text("Scan", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_bottom_scan")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.DESIGNER,
                            onClick = { onNavigate(AppDestination.DESIGNER) },
                            icon = { Icon(Icons.Default.Palette, contentDescription = "Designer", modifier = Modifier.size(20.dp)) },
                            label = { Text("Design", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_bottom_designer")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.SAMPLES,
                            onClick = { onNavigate(AppDestination.SAMPLES) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Samples", modifier = Modifier.size(20.dp)) },
                            label = { Text("Samples", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_bottom_samples")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.HISTORY,
                            onClick = { onNavigate(AppDestination.HISTORY) },
                            icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(20.dp)) },
                            label = { Text("History", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_bottom_history")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun OfflineBadge() {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
        shape = RoundedCornerShape(50),
        modifier = Modifier.testTag("badge_offline_status")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = "OFFLINE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

fun getDestinationTitle(destination: AppDestination): String {
    return when (destination) {
        AppDestination.HOME -> "AlexQr"
        AppDestination.SCANNER -> "QR Scanner"
        AppDestination.DESIGNER -> "QR Studio"
        AppDestination.SAMPLES -> "Sample Hub"
        AppDestination.HISTORY -> "History"
        AppDestination.SETTINGS -> "Personalization"
        AppDestination.ABOUT_DEV -> "About Developer"
    }
}
