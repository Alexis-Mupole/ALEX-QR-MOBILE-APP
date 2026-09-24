package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val badge: String
)

@Composable
fun OnboardingDialog(
    onDismiss: (dontShowAgain: Boolean) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var dontShowAgain by remember { mutableStateOf(true) }

    val steps = remember {
        listOf(
            OnboardingStep(
                title = "Instant Offline Scanner",
                subtitle = "Hardware-Accelerated Camera & Gallery",
                description = "Scan any QR code or barcode instantly via live camera or gallery pictures. Uses local ML Kit with zero internet permissions, full torch flash control, and ultra-fast detection.",
                icon = Icons.Default.QrCodeScanner,
                badge = "100% OFFLINE"
            ),
            OnboardingStep(
                title = "Pro QR Studio & Brand Logos",
                subtitle = "Custom Styles, Logos & Bottom Text",
                description = "Generate scannable QR codes for Wi-Fi, contacts, URLs, phone numbers, and memos. Embed your brand logo in the center and add bold 'SCAN ME' captions with ISO-standard Error Correction H.",
                icon = Icons.Default.Palette,
                badge = "LEVEL H ACCURACY"
            ),
            OnboardingStep(
                title = "Personalize Colors & Save Power",
                subtitle = "Custom Interfaces & Battery Optimization",
                description = "Tailor the interface with vibrant color accents (Electric Blue, Emerald Green, Sunset Orange, Royal Violet). Enable Ultra-Low Power Mode to conserve battery during long scan sessions.",
                icon = Icons.Default.BatteryChargingFull,
                badge = "POWER EFFICIENT"
            ),
            OnboardingStep(
                title = "Privacy First & Production Ready",
                subtitle = "Your Data Never Leaves Your Device",
                description = "All scans, generated codes, and history are stored locally in on-device Room database. No analytics, no ads, no trackers, and zero external network calls.",
                icon = Icons.Default.Security,
                badge = "ZERO DATA LEAK"
            )
        )
    }

    val step = steps[currentStep]
    val isLast = currentStep == steps.size - 1

    Dialog(
        onDismissRequest = { onDismiss(dontShowAgain) },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row: Step indicators & Skip button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        steps.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == currentStep) 20.dp else 8.dp, 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (index == currentStep) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }

                    TextButton(
                        onClick = { onDismiss(dontShowAgain) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Skip", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Icon Circle with Badge
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "${currentStep + 1}/${steps.size}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = step.badge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = step.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // "Don't show again" Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = dontShowAgain,
                        onCheckedChange = { dontShowAgain = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Do not show this guide again",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back")
                        }
                    }

                    Button(
                        onClick = {
                            if (isLast) {
                                onDismiss(dontShowAgain)
                            } else {
                                currentStep++
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(if (currentStep > 0) 1.4f else 2f)
                    ) {
                        Text(if (isLast) "Get Started" else "Next")
                    }
                }
            }
        }
    }
}
