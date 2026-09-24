package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CenterLogoType
import com.example.model.DotShape
import com.example.model.EyeStyle
import com.example.model.QrStyleConfig
import com.example.ui.components.StyledQrCanvas
import com.example.viewmodel.DesignerInputType
import com.example.viewmodel.MainViewModel

@Composable
fun DesignerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentInputType by viewModel.designerInputType.collectAsStateWithLifecycle()
    val qrMatrix by viewModel.qrMatrix.collectAsStateWithLifecycle()
    val styleConfig by viewModel.styleConfig.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 760.dp

        if (isWide) {
            // Tablet / Desktop 2-column layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Left Column: Controls and inputs
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .verticalScroll(rememberScrollState())
                ) {
                    DesignerControlsContent(viewModel, currentInputType, styleConfig)
                }

                // Right Column: Sticky Live Preview & Action Buttons
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QrPreviewAndActions(
                        viewModel = viewModel,
                        qrMatrix = qrMatrix,
                        styleConfig = styleConfig,
                        context = context
                    )
                }
            }
        } else {
            // Compact Smartphone layout (single scrolling column)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Live preview at top
                QrPreviewAndActions(
                    viewModel = viewModel,
                    qrMatrix = qrMatrix,
                    styleConfig = styleConfig,
                    context = context,
                    isCompact = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Controls below
                DesignerControlsContent(viewModel, currentInputType, styleConfig)

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun QrPreviewAndActions(
    viewModel: MainViewModel,
    qrMatrix: Array<BooleanArray>?,
    styleConfig: QrStyleConfig,
    context: android.content.Context,
    isCompact: Boolean = false
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live QR Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.recomputeMatrix() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Styled Canvas
            StyledQrCanvas(
                matrix = qrMatrix,
                style = styleConfig,
                modifier = Modifier
                    .size(if (isCompact) 240.dp else 280.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.saveQrToGallery(context) { uri ->
                            if (uri != null) {
                                Toast.makeText(context, "Saved to Pictures/AlexQr!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_to_device_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Image")
                }

                FilledTonalButton(
                    onClick = { viewModel.shareCurrentQr(context) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_code_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    viewModel.saveCurrentDesignToHistory(context) { success ->
                        if (success) {
                            Toast.makeText(context, "Design saved to History!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to History Library")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DesignerControlsContent(
    viewModel: MainViewModel,
    currentInputType: DesignerInputType,
    styleConfig: QrStyleConfig
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. Content Type Selector
        Text(
            text = "SELECT DATA TYPE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InputChip(
                selected = currentInputType == DesignerInputType.TEXT,
                onClick = { viewModel.setDesignerInputType(DesignerInputType.TEXT) },
                label = { Text("Text") },
                leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null) }
            )
            InputChip(
                selected = currentInputType == DesignerInputType.URL,
                onClick = { viewModel.setDesignerInputType(DesignerInputType.URL) },
                label = { Text("URL / Link") },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
            )
            InputChip(
                selected = currentInputType == DesignerInputType.PHONE,
                onClick = { viewModel.setDesignerInputType(DesignerInputType.PHONE) },
                label = { Text("Phone") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
            )
            InputChip(
                selected = currentInputType == DesignerInputType.WIFI,
                onClick = { viewModel.setDesignerInputType(DesignerInputType.WIFI) },
                label = { Text("Wi-Fi") },
                leadingIcon = { Icon(Icons.Default.Wifi, contentDescription = null) }
            )
            InputChip(
                selected = currentInputType == DesignerInputType.CONTACT,
                onClick = { viewModel.setDesignerInputType(DesignerInputType.CONTACT) },
                label = { Text("vCard") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Input Fields based on Type
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (currentInputType) {
                    DesignerInputType.TEXT -> {
                        val text by viewModel.rawText.collectAsStateWithLifecycle()
                        OutlinedTextField(
                            value = text,
                            onValueChange = {
                                viewModel.rawText.value = it
                                viewModel.recomputeMatrix()
                            },
                            label = { Text("Text or Memo") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6
                        )
                    }
                    DesignerInputType.URL -> {
                        val url by viewModel.urlText.collectAsStateWithLifecycle()
                        OutlinedTextField(
                            value = url,
                            onValueChange = {
                                viewModel.urlText.value = it
                                viewModel.recomputeMatrix()
                            },
                            label = { Text("Website URL") },
                            placeholder = { Text("https://example.com") },
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    DesignerInputType.PHONE -> {
                        val phone by viewModel.phoneNumber.collectAsStateWithLifecycle()
                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                viewModel.phoneNumber.value = it
                                viewModel.recomputeMatrix()
                            },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+1 (555) 000-0000") },
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    DesignerInputType.WIFI -> {
                        val ssid by viewModel.wifiSsid.collectAsStateWithLifecycle()
                        val pass by viewModel.wifiPassword.collectAsStateWithLifecycle()
                        val auth by viewModel.wifiAuthType.collectAsStateWithLifecycle()
                        val hidden by viewModel.wifiHidden.collectAsStateWithLifecycle()
                        var showPassword by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = ssid,
                            onValueChange = {
                                viewModel.wifiSsid.value = it
                                viewModel.recomputeMatrix()
                            },
                            label = { Text("Network Name (SSID)") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = pass,
                            onValueChange = {
                                viewModel.wifiPassword.value = it
                                viewModel.recomputeMatrix()
                            },
                            label = { Text("Wi-Fi Password") },
                            shape = RoundedCornerShape(14.dp),
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Security Type:", style = MaterialTheme.typography.labelMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            listOf("WPA", "WEP", "NONE").forEach { type ->
                                InputChip(
                                    selected = auth == type,
                                    onClick = {
                                        viewModel.wifiAuthType.value = type
                                        viewModel.recomputeMatrix()
                                    },
                                    label = { Text(type) }
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.wifiHidden.value = !hidden
                                viewModel.recomputeMatrix()
                            }
                        ) {
                            Checkbox(
                                checked = hidden,
                                onCheckedChange = {
                                    viewModel.wifiHidden.value = it
                                    viewModel.recomputeMatrix()
                                }
                            )
                            Text("Hidden Network", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    DesignerInputType.CONTACT -> {
                        val name by viewModel.contactName.collectAsStateWithLifecycle()
                        val phone by viewModel.contactPhone.collectAsStateWithLifecycle()
                        val email by viewModel.contactEmail.collectAsStateWithLifecycle()

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                viewModel.contactName.value = it
                                viewModel.recomputeMatrix()
                            },
                            label = { Text("Full Name") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                viewModel.contactPhone.value = it
                                viewModel.recomputeMatrix()
                            },
                            label = { Text("Phone Number") },
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                viewModel.contactEmail.value = it
                                viewModel.recomputeMatrix()
                            },
                            label = { Text("Email Address") },
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Styling & Customization Section
        Text(
            text = "STYLING & CUSTOMIZATION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Gradient Foreground Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Matrix Color Gradient", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("Blend foreground across diagonal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = styleConfig.useGradient,
                        onCheckedChange = {
                            viewModel.updateStyleConfig(styleConfig.copy(useGradient = it))
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerDefaults.color.copy(alpha = 0.5f))

                // Primary Color Palette Presets
                Text("Primary Foreground Color", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ColorPresetRow(
                    selectedColor = styleConfig.primaryColor,
                    colors = listOf(
                        0xFF0A0E17, // Classic Pitch Black
                        0xFF00E5FF, // Electric Cyan
                        0xFF4338CA, // Deep Indigo
                        0xFF7C3AED, // Royal Violet
                        0xFF059669, // Emerald
                        0xFFDC2626, // Crimson
                        0xFFD97706  // Amber
                    ),
                    onSelect = { color ->
                        viewModel.updateStyleConfig(styleConfig.copy(primaryColor = color))
                    }
                )

                if (styleConfig.useGradient) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Secondary Gradient Color", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorPresetRow(
                        selectedColor = styleConfig.secondaryColor,
                        colors = listOf(
                            0xFF1E3A8A, // Navy
                            0xFF9333EA, // Violet
                            0xFFEC4899, // Pink
                            0xFF06B6D4, // Cyan
                            0xFF10B981, // Mint
                            0xFFF97316  // Orange
                        ),
                        onSelect = { color ->
                            viewModel.updateStyleConfig(styleConfig.copy(secondaryColor = color))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Background Color
                Text("Background Color", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ColorPresetRow(
                    selectedColor = styleConfig.backgroundColor,
                    colors = listOf(
                        0xFFFFFFFF, // Pure White
                        0xFFF8FAFC, // Crisp Light Slate
                        0xFFF0FDF4, // Soft Mint
                        0xFFFFFBEB, // Warm Cream
                        0xFF111827  // Dark Slate
                    ),
                    onSelect = { color ->
                        viewModel.updateStyleConfig(styleConfig.copy(backgroundColor = color))
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = DividerDefaults.color.copy(alpha = 0.5f))

                // Dot Shape Styling
                Text("Dot Shape Styling", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Select matrix module geometry", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Pair(DotShape.SQUARE, "Square"),
                        Pair(DotShape.ROUNDED, "Rounded"),
                        Pair(DotShape.CIRCLES, "Circles")
                    ).forEach { (shape, label) ->
                        InputChip(
                            selected = styleConfig.dotShape == shape,
                            onClick = {
                                viewModel.updateStyleConfig(styleConfig.copy(dotShape = shape))
                            },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Eye Styling
                Text("Eye Corner Styling", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Finder pattern corner design", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Pair(EyeStyle.SQUARE, "Square Eyes"),
                        Pair(EyeStyle.ROUNDED, "Rounded Eyes"),
                        Pair(EyeStyle.CIRCLES, "Concentric Circles")
                    ).forEach { (eye, label) ->
                        InputChip(
                            selected = styleConfig.eyeStyle == eye,
                            onClick = {
                                viewModel.updateStyleConfig(styleConfig.copy(eyeStyle = eye))
                            },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = DividerDefaults.color.copy(alpha = 0.5f))

                // Center Brand Logo
                Text("Center Brand Logo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("High Error Correction (Level H) ensures 100% scanability with embedded logos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                val logoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    if (uri != null) {
                        viewModel.updateStyleConfig(
                            styleConfig.copy(
                                centerLogo = CenterLogoType.CUSTOM,
                                customLogoUri = uri.toString()
                            )
                        )
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair(CenterLogoType.NONE, "No Logo"),
                        Pair(CenterLogoType.ALEX_QR, "AlexQr"),
                        Pair(CenterLogoType.LINK, "Link"),
                        Pair(CenterLogoType.WIFI, "Wi-Fi"),
                        Pair(CenterLogoType.HEART, "Heart"),
                        Pair(CenterLogoType.STAR, "Star"),
                        Pair(CenterLogoType.USER, "Contact"),
                        Pair(CenterLogoType.CUSTOM, "Custom Image...")
                    ).forEach { (logoType, label) ->
                        InputChip(
                            selected = styleConfig.centerLogo == logoType,
                            onClick = {
                                if (logoType == CenterLogoType.CUSTOM) {
                                    logoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    viewModel.updateStyleConfig(styleConfig.copy(centerLogo = logoType))
                                }
                            },
                            label = { Text(label) }
                        )
                    }
                }

                if (styleConfig.centerLogo == CenterLogoType.CUSTOM && styleConfig.customLogoUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Custom gallery logo attached",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            FilledTonalButton(
                                onClick = {
                                    viewModel.updateStyleConfig(
                                        styleConfig.copy(centerLogo = CenterLogoType.NONE, customLogoUri = null)
                                    )
                                },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Remove", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = DividerDefaults.color.copy(alpha = 0.5f))

                // Bottom Call-To-Action Text (e.g. "SCAN ME")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Call-To-Action Text Under QR",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "E.g., 'SCAN ME' badge directly under QR code",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = styleConfig.showBottomText,
                        onCheckedChange = { isChecked ->
                            viewModel.updateStyleConfig(styleConfig.copy(showBottomText = isChecked))
                        }
                    )
                }

                if (styleConfig.showBottomText) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = styleConfig.bottomText,
                        onValueChange = { newText ->
                            viewModel.updateStyleConfig(styleConfig.copy(bottomText = newText))
                        },
                        label = { Text("Bottom Caption Text") },
                        placeholder = { Text("SCAN ME") },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Quick Presets:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("SCAN ME", "VISIT US", "CONNECT NOW", "PAY HERE", "AlexQr").forEach { preset ->
                            InputChip(
                                selected = styleConfig.bottomText == preset,
                                onClick = {
                                    viewModel.updateStyleConfig(styleConfig.copy(bottomText = preset))
                                },
                                label = { Text(preset, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPresetRow(
    selectedColor: Long,
    colors: List<Long>,
    onSelect: (Long) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        colors.forEach { col ->
            val isSelected = selectedColor == col
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(col))
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
                    .clickable { onSelect(col) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    val iconTint = if (col == 0xFFFFFFFF || col == 0xFFF8FAFC || col == 0xFFF0FDF4 || col == 0xFFFFFBEB) Color.Black else Color.White
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
