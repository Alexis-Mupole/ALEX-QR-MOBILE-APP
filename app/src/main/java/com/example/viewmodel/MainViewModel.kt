package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AlexQrApp
import com.example.data.DarkThemeConfig
import com.example.data.PreferencesManager
import com.example.data.ThemePalette
import com.example.data.local.QrRecordEntity
import com.example.generator.QrCodeGenerator
import com.example.model.CenterLogoType
import com.example.model.DotShape
import com.example.model.EyeStyle
import com.example.model.ParsedQrContent
import com.example.model.QrStyleConfig
import com.example.model.SampleQrItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination {
    HOME,
    SCANNER,
    DESIGNER,
    SAMPLES,
    HISTORY,
    SETTINGS,
    ABOUT_DEV
}

enum class DesignerInputType {
    TEXT,
    URL,
    PHONE,
    WIFI,
    CONTACT
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AlexQrApp).repository
    private val prefManager = PreferencesManager(application)

    // Navigation & Destination
    private val _currentDestination = MutableStateFlow(AppDestination.HOME)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    // ---------------- PERSONALIZATION & PREFERENCES ----------------
    private val _selectedPalette = MutableStateFlow(prefManager.selectedPalette)
    val selectedPalette: StateFlow<ThemePalette> = _selectedPalette.asStateFlow()

    fun setThemePalette(palette: ThemePalette) {
        _selectedPalette.value = palette
        prefManager.selectedPalette = palette
    }

    private val _darkThemeConfig = MutableStateFlow(prefManager.darkThemeConfig)
    val darkThemeConfig: StateFlow<DarkThemeConfig> = _darkThemeConfig.asStateFlow()

    fun setDarkThemeConfig(config: DarkThemeConfig) {
        _darkThemeConfig.value = config
        prefManager.darkThemeConfig = config
    }

    private val _powerSaverMode = MutableStateFlow(prefManager.powerSaverMode)
    val powerSaverMode: StateFlow<Boolean> = _powerSaverMode.asStateFlow()

    fun setPowerSaverMode(enabled: Boolean) {
        _powerSaverMode.value = enabled
        prefManager.powerSaverMode = enabled
    }

    private val _vibrateOnScan = MutableStateFlow(prefManager.vibrateOnScan)
    val vibrateOnScan: StateFlow<Boolean> = _vibrateOnScan.asStateFlow()

    fun setVibrateOnScan(enabled: Boolean) {
        _vibrateOnScan.value = enabled
        prefManager.vibrateOnScan = enabled
    }

    private val _autoCopyOnScan = MutableStateFlow(prefManager.autoCopyOnScan)
    val autoCopyOnScan: StateFlow<Boolean> = _autoCopyOnScan.asStateFlow()

    fun setAutoCopyOnScan(enabled: Boolean) {
        _autoCopyOnScan.value = enabled
        prefManager.autoCopyOnScan = enabled
    }

    // First-time interactive guide
    private val _showOnboarding = MutableStateFlow(!prefManager.isOnboardingCompleted)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    fun completeOnboarding(dontShowAgain: Boolean) {
        _showOnboarding.value = false
        if (dontShowAgain) {
            prefManager.isOnboardingCompleted = true
        }
    }

    fun showOnboardingGuide() {
        _showOnboarding.value = true
    }

    // ---------------- MODULE 1: SCANNER STATE ----------------
    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    fun toggleTorch() {
        _isTorchOn.value = !_isTorchOn.value
    }

    fun setTorch(on: Boolean) {
        _isTorchOn.value = on
    }

    private val _scannedItem = MutableStateFlow<ParsedQrContent?>(null)
    val scannedItem: StateFlow<ParsedQrContent?> = _scannedItem.asStateFlow()

    private val _showScanSheet = MutableStateFlow(false)
    val showScanSheet: StateFlow<Boolean> = _showScanSheet.asStateFlow()

    private var lastScannedTimestamp = 0L

    fun onBarcodeScanned(raw: String) {
        // Debounce repeated scans within 1.5 seconds
        val now = System.currentTimeMillis()
        if (now - lastScannedTimestamp < 1500) return
        lastScannedTimestamp = now

        val parsed = ParsedQrContent.parse(raw)
        _scannedItem.value = parsed
        _showScanSheet.value = true

        // Haptic feedback if enabled
        if (_vibrateOnScan.value) {
            triggerScanVibration()
        }

        // Auto-copy to clipboard if enabled
        if (_autoCopyOnScan.value) {
            copyToClipboard(raw)
        }

        // Automatically log into Room database
        viewModelScope.launch {
            val record = QrRecordEntity(
                recordType = "SCANNED",
                rawContent = raw,
                contentType = parsed.contentType,
                stylingParameters = "{}",
                timestamp = now,
                title = parsed.displayTitle
            )
            repository.insertRecord(record)
        }
    }

    private fun triggerScanVibration() {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50L)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun copyToClipboard(text: String) {
        try {
            val app = getApplication<Application>()
            val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.setPrimaryClip(ClipData.newPlainText("AlexQr Scanned", text))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissScanSheet() {
        _showScanSheet.value = false
    }

    // ---------------- MODULE 2: DESIGNER GENERATOR STATE ----------------
    private val _designerInputType = MutableStateFlow(DesignerInputType.TEXT)
    val designerInputType: StateFlow<DesignerInputType> = _designerInputType.asStateFlow()

    fun setDesignerInputType(type: DesignerInputType) {
        _designerInputType.value = type
        recomputeMatrix()
    }

    val rawText = MutableStateFlow("Welcome to AlexQr Pro Suite")
    val urlText = MutableStateFlow("https://alexismupole.dev")
    val phoneNumber = MutableStateFlow("+1234567890")
    val wifiSsid = MutableStateFlow("Home_Network_5G")
    val wifiPassword = MutableStateFlow("AlexQrSecret2026")
    val wifiAuthType = MutableStateFlow("WPA")
    val wifiHidden = MutableStateFlow(false)
    val contactName = MutableStateFlow("Alexis Mupole")
    val contactPhone = MutableStateFlow("+243 999 000 000")
    val contactEmail = MutableStateFlow("contact@alexismupole.dev")

    // QR Styling Configuration
    private val _styleConfig = MutableStateFlow(
        QrStyleConfig(
            primaryColor = 0xFF0A0E17,
            secondaryColor = 0xFF1E3A8A,
            useGradient = false,
            backgroundColor = 0xFFFFFFFF,
            dotShape = DotShape.SQUARE,
            eyeStyle = EyeStyle.SQUARE
        )
    )
    val styleConfig: StateFlow<QrStyleConfig> = _styleConfig.asStateFlow()

    fun updateStyleConfig(newConfig: QrStyleConfig) {
        _styleConfig.value = newConfig
    }

    private val _qrMatrix = MutableStateFlow<Array<BooleanArray>?>(null)
    val qrMatrix: StateFlow<Array<BooleanArray>?> = _qrMatrix.asStateFlow()

    fun getComputedContent(): String {
        return when (_designerInputType.value) {
            DesignerInputType.TEXT -> rawText.value.ifBlank { " " }
            DesignerInputType.URL -> {
                val u = urlText.value.trim()
                if (u.startsWith("http://") || u.startsWith("https://")) u else "https://$u"
            }
            DesignerInputType.PHONE -> "tel:${phoneNumber.value.trim()}"
            DesignerInputType.WIFI -> {
                val auth = wifiAuthType.value
                val p = wifiPassword.value
                val h = wifiHidden.value
                "WIFI:S:${wifiSsid.value};T:$auth;P:$p;H:$h;;"
            }
            DesignerInputType.CONTACT -> {
                "BEGIN:VCARD\nVERSION:3.0\nFN:${contactName.value}\nTEL:${contactPhone.value}\nEMAIL:${contactEmail.value}\nEND:VCARD"
            }
        }
    }

    fun recomputeMatrix() {
        val content = getComputedContent()
        _qrMatrix.value = QrCodeGenerator.generateMatrix(content)
    }

    init {
        recomputeMatrix()
    }

    fun saveCurrentDesignToHistory(context: Context, onSaved: (Boolean) -> Unit) {
        viewModelScope.launch {
            val content = getComputedContent()
            val parsed = ParsedQrContent.parse(content)
            val currentStyle = _styleConfig.value

            val record = QrRecordEntity(
                recordType = "GENERATED",
                rawContent = content,
                contentType = parsed.contentType,
                stylingParameters = currentStyle.toJson(),
                timestamp = System.currentTimeMillis(),
                title = parsed.displayTitle
            )
            val id = repository.insertRecord(record)
            onSaved(id > 0)
        }
    }

    fun saveQrToGallery(context: Context, onResult: (Uri?) -> Unit) {
        val matrix = _qrMatrix.value ?: return onResult(null)
        val style = _styleConfig.value
        val customLogo = if (style.centerLogo == CenterLogoType.CUSTOM && style.customLogoUri != null) {
            QrCodeGenerator.decodeBitmapFromUri(context, style.customLogoUri, 256)
        } else null
        val bitmap = QrCodeGenerator.renderStyledBitmap(matrix, style, 1024, customLogo)
        val uri = QrCodeGenerator.saveBitmapToMediaStore(context, bitmap, getComputedContent())

        // Also save record to generated history if not already saved
        viewModelScope.launch {
            val content = getComputedContent()
            val parsed = ParsedQrContent.parse(content)
            repository.insertRecord(
                QrRecordEntity(
                    recordType = "GENERATED",
                    rawContent = content,
                    contentType = parsed.contentType,
                    stylingParameters = style.toJson(),
                    timestamp = System.currentTimeMillis(),
                    title = parsed.displayTitle
                )
            )
        }
        onResult(uri)
    }

    fun shareCurrentQr(context: Context) {
        val matrix = _qrMatrix.value ?: return
        val style = _styleConfig.value
        val customLogo = if (style.centerLogo == CenterLogoType.CUSTOM && style.customLogoUri != null) {
            QrCodeGenerator.decodeBitmapFromUri(context, style.customLogoUri, 256)
        } else null
        val bitmap = QrCodeGenerator.renderStyledBitmap(matrix, style, 1024, customLogo)
        QrCodeGenerator.shareQrCode(context, bitmap, getComputedContent())
    }

    // ---------------- MODULE 3: PERSISTENT HISTORY ----------------
    val historySearchQuery = MutableStateFlow("")

    val scannedHistory: StateFlow<List<QrRecordEntity>> = combine(
        repository.getRecordsByType("SCANNED"),
        historySearchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.rawContent.contains(query, ignoreCase = true) ||
            it.title.contains(query, ignoreCase = true) ||
            it.contentType.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val generatedHistory: StateFlow<List<QrRecordEntity>> = combine(
        repository.getRecordsByType("GENERATED"),
        historySearchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.rawContent.contains(query, ignoreCase = true) ||
            it.title.contains(query, ignoreCase = true) ||
            it.contentType.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteRecord(record: QrRecordEntity) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    fun deleteRecordById(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearHistory(type: String? = null) {
        viewModelScope.launch {
            if (type == null) {
                repository.clearAll()
            } else {
                repository.clearByType(type)
            }
        }
    }

    // Re-rendering custom styled QR code from history parameters
    private val _previewHistoryRecord = MutableStateFlow<QrRecordEntity?>(null)
    val previewHistoryRecord: StateFlow<QrRecordEntity?> = _previewHistoryRecord.asStateFlow()

    fun showHistoryDetail(record: QrRecordEntity) {
        _previewHistoryRecord.value = record
    }

    fun dismissHistoryDetail() {
        _previewHistoryRecord.value = null
    }

    fun loadRecordIntoDesigner(record: QrRecordEntity) {
        val parsed = ParsedQrContent.parse(record.rawContent)
        val style = QrStyleConfig.fromJson(record.stylingParameters)

        when (parsed.contentType) {
            "URL" -> {
                _designerInputType.value = DesignerInputType.URL
                urlText.value = parsed.url ?: record.rawContent
            }
            "WIFI" -> {
                _designerInputType.value = DesignerInputType.WIFI
                wifiSsid.value = parsed.wifiSsid ?: ""
                wifiPassword.value = parsed.wifiPassword ?: ""
                wifiAuthType.value = parsed.wifiAuthType ?: "WPA"
                wifiHidden.value = parsed.wifiHidden
            }
            "PHONE" -> {
                _designerInputType.value = DesignerInputType.PHONE
                phoneNumber.value = parsed.phone ?: record.rawContent
            }
            "CONTACT" -> {
                _designerInputType.value = DesignerInputType.CONTACT
                contactName.value = parsed.contactName ?: ""
                contactPhone.value = parsed.contactPhone ?: ""
                contactEmail.value = parsed.contactEmail ?: ""
            }
            else -> {
                _designerInputType.value = DesignerInputType.TEXT
                rawText.value = record.rawContent
            }
        }

        _styleConfig.value = style
        recomputeMatrix()
        _currentDestination.value = AppDestination.DESIGNER
    }

    // ---------------- MODULE 4: SAMPLES ----------------
    fun loadSampleIntoDesigner(sample: SampleQrItem) {
        val parsed = ParsedQrContent.parse(sample.content)
        when (parsed.contentType) {
            "URL" -> {
                _designerInputType.value = DesignerInputType.URL
                urlText.value = parsed.url ?: sample.content
            }
            "WIFI" -> {
                _designerInputType.value = DesignerInputType.WIFI
                wifiSsid.value = parsed.wifiSsid ?: ""
                wifiPassword.value = parsed.wifiPassword ?: ""
                wifiAuthType.value = parsed.wifiAuthType ?: "WPA"
                wifiHidden.value = parsed.wifiHidden
            }
            "PHONE" -> {
                _designerInputType.value = DesignerInputType.PHONE
                phoneNumber.value = parsed.phone ?: sample.content
            }
            "CONTACT" -> {
                _designerInputType.value = DesignerInputType.CONTACT
                contactName.value = parsed.contactName ?: ""
                contactPhone.value = parsed.contactPhone ?: ""
                contactEmail.value = parsed.contactEmail ?: ""
            }
            else -> {
                _designerInputType.value = DesignerInputType.TEXT
                rawText.value = sample.content
            }
        }

        _styleConfig.value = sample.defaultStyle
        recomputeMatrix()
        _currentDestination.value = AppDestination.DESIGNER
    }
}
