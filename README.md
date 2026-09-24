# AlexQr

<p align="center">
  <strong>Fast, Offline, Privacy-First QR Code Scanner & Custom Designer for Android</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203-6750A4" alt="Material 3" />
  <img src="https://img.shields.io/badge/Camera-CameraX-009688" alt="CameraX" />
  <img src="https://img.shields.io/badge/Vision-Google%20ML%20Kit-FF6F00" alt="ML Kit" />
  <img src="https://img.shields.io/badge/Generator-ZXing%20Core-007ACC" alt="ZXing" />
  <img src="https://img.shields.io/badge/Database-Room%20SQLite-00599C" alt="Room" />
  <img src="https://img.shields.io/badge/Internet%20Permission-NONE%20(100%25%20Offline)-brightgreen" alt="100% Offline" />
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License" />
</p>

---

## 📖 Overview

**AlexQr** is an open-source, production-ready Android application built with modern **Kotlin** and **Jetpack Compose (Material 3)**. It combines real-time camera scanning powered by **Google ML Kit Vision & CameraX** with an advanced **QR Code Designer Engine** powered by **ZXing BitMatrix** with high error correction (`Level H`).

Unlike cloud-dependent QR utilities that track user scans or inject advertisements, **AlexQr is strictly 100% offline**:
- **Zero Internet Permissions**: `android.permission.INTERNET` is **not** declared in `AndroidManifest.xml`.
- **Zero Remote Tracking**: Every scan, code generation, and history entry stays strictly inside your device's encrypted local SQLite database via Android Room.
- **Zero Battery Waste**: Includes a dedicated Power-Saver mode that throttles camera frame processing and pauses heavy animations when your battery is low.

Created with passion by **MUPOLE UWIZEYE Alexis** ([alexismupole.dev](https://alexismupole.dev)).

---

## ✨ Key Features

### 📷 1. High-Performance Real-Time Scanner
- **ML Kit On-Device Barcode Engine**: Instant zero-lag detection of standard QR codes, Micro-QR, and 2D barcodes.
- **CameraX Lifecycle-Safe Architecture**: Automatically unbinds and rebinds camera sessions during app suspension to prevent memory leaks and background battery drain.
- **Hardware Controls**:
  - Direct hardware torch/flash toggle.
  - Pinch-to-zoom and double-tap zoom gestures.
  - Tap-to-focus with visual targeting ring.
- **Laser Scanner Overlay**: Smooth, battery-optimized animated laser reticle indicating the scanning boundary.

### 🎨 2. Custom QR Designer Engine
- **ZXing BitMatrix with Error Correction Level H**:
  - Codes remain 100% scan-ready even when logos or custom dot patterns cover up to 30% of the surface area.
- **Customizable Dot Geometries**:
  - Square (Classic)
  - Rounded Rectangles
  - Circles / Smooth Dots
- **Customizable Eye Styles**:
  - Square Finder Corners
  - Rounded Finder Corners
  - Smooth Circular Finder Corners
- **Color Palettes & Custom Hex**:
  - Independent Foreground and Background color pickers.
  - Pre-curated palettes (Indigo, Emerald, Crimson, Royal Purple, Amber, Slate, Pitch Black, Clean White).
- **Center Logo Integration**:
  - Built-in vector icons: Star, Heart, QR Shield, Link, Shield Check.
  - Custom image gallery picker: Embed your own personal avatar, brand icon, or photo in the center.
- **Bottom Call-to-Action Text Banner**:
  - Add customizable text banners below the QR code (e.g., *"SCAN ME"*, *"PAY HERE"*, *"CONNECT TO WIFI"*, *"VISIT WEBSITE"*).
  - Configurable banner background and text colors.
- **High-Resolution Export**:
  - Anti-aliased high-DPI Bitmap generation.
  - Instant native Android system share dialog or save directly to local storage via Android FileProvider.

### 🧠 3. Smart Parser & Action Dispatcher
Scanned codes are automatically analyzed and partitioned into rich semantic types with one-tap action handlers:
- **🌐 Web URLs**: Direct browser launch.
- **📶 Wi-Fi Networks**: SSID, Password, and Security Type (WPA/WEP/None) with quick-copy.
- **📇 Contacts (vCard / MeCard)**: Name, phone, email, company, and address with one-tap contact import.
- **💬 SMS & Text**: Pre-populated phone number and SMS body with direct messaging app intent.
- **📞 Phone Numbers**: Direct dialer intent.
- **✉️ Email**: Recipient, Subject, and Body pre-filled into default mail client.
- **📍 Geo Coordinates**: Latitude & Longitude mapped to Google Maps / default navigation app.
- **💰 Cryptocurrencies**: Bitcoin (`bitcoin:`) and Ethereum (`ethereum:`) addresses with one-tap wallet dispatch.
- **📄 Plain Text**: Markdown and text viewer with clipboard copy.

### 📚 4. Curated Samples Hub
- Built-in library of ready-to-test templates:
  - Office Wi-Fi Network
  - Developer Portfolio (alexismupole.dev)
  - Business Executive vCard
  - Bitcoin Cold Wallet Address
  - SMS Helpdesk Dispatch
  - Conference Location Geo-Pin
- Tap any template to immediately load it into the Designer or preview its parsed action sheet.

### 💾 5. Persistent Local History & Bookmarks
- **Local Room Database (SQLite)**: Fully persistent record of all scanned and generated codes.
- **Favorite / Bookmark**: Pin important codes for quick retrieval.
- **Live Search & Filter**: Filter by scan vs. generate, or search across raw text and timestamps.
- **Export & Privacy Controls**:
  - Export history to CSV.
  - Bulk wipe history with a single confirmation.

### ⚙️ 6. Full App Personalization & Power-Saver
- **Multi-Theme Dynamic Color Palettes**:
  - 🌌 Cyber Indigo
  - 🌿 Emerald Forest
  - 🌅 Sunset Crimson
  - 🔮 Royal Purple
  - ☀️ Amber Gold
  - 🪨 Minimal Slate
- **Theme Modes**: Dark Mode, Light Mode, and System Default.
- **Power Saver Mode**: Limits camera analysis frame rate and disables laser scanner animation to maximize battery life on long scanning sessions.
- **First-Run Onboarding Guide**: Interactive tutorial explaining key features, dismissible with a *"Do not show again"* option.
- **Responsive UI Across Devices**: Adaptive layouts optimized for compact phones, foldables, and large tablet screens with unified 16.dp–24.dp rounded surfaces.

---

## 🏗️ Architecture & Tech Stack

AlexQr follows **Clean Architecture** and the recommended Android **MVVM (Model-View-ViewModel)** architectural pattern:

```
app/src/main/java/com/example/
├── AlexQrApp.kt                     # Application class initializing Room & preferences
├── MainActivity.kt                  # Single-activity host with Edge-to-Edge support
├── data/
│   ├── PreferencesManager.kt       # Persistent settings (theme, power-saver, onboarding)
│   └── local/
│       ├── AppDatabase.kt          # Room SQLite Database declaration
│       ├── QrRecordDao.kt          # Room DAO with reactive Kotlin Flows
│       ├── QrRecordEntity.kt       # Persistent QR Record entity model
│       └── QrRepository.kt         # Single source of truth repository
├── generator/
│   └── QrCodeGenerator.kt          # ZXing BitMatrix & Android Canvas custom renderer
├── model/
│   ├── ParsedQrContent.kt          # Semantic QR parser & type classifiers
│   ├── QrStyleConfig.kt            # Designer style state (dots, eyes, colors, logo, text)
│   └── SampleQrItem.kt             # Curated sample items catalog
├── scanner/
│   └── CameraView.kt               # CameraX PreviewView with ML Kit BarcodeAnalyzer
├── ui/
│   ├── components/
│   │   ├── AppResponsiveScaffold.kt# Adaptive TopBar, Drawer, and Navigation Rail
│   │   ├── LaserScanOverlay.kt     # Animated laser reticle
│   │   ├── OnboardingDialog.kt     # First-run interactive guide
│   │   ├── QrDetailBottomSheet.kt  # BottomSheet modal displaying parsed scan actions
│   │   └── StyledQrCanvas.kt       # Interactive Compose Canvas for live QR preview
│   ├── screens/
│   │   ├── AboutDeveloperScreen.kt # Developer bio and portfolio page
│   │   ├── DesignerScreen.kt       # Full visual QR code studio
│   │   ├── HistoryScreen.kt        # Searchable scan/generate local history
│   │   ├── HomeScreen.kt           # Production dashboard & hero overview
│   │   ├── SamplesHubScreen.kt     # Curated templates catalog
│   │   ├── ScannerScreen.kt        # Real-time camera viewfinder
│   │   └── SettingsScreen.kt       # Color themes, power saver, and preferences
│   └── theme/
│       ├── Color.kt                # Multi-palette color definitions
│       ├── Theme.kt                # Dynamic M3 ColorScheme provider
│       └── Type.kt                 # Material 3 Typography system
└── viewmodel/
    └── MainViewModel.kt            # Central UI state coordinator and Coroutine dispatcher
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2+) or newer
- **JDK**: Java 17 or Java 21
- **Android SDK**: Compile SDK `36`, Minimum SDK `24` (Android 7.0+)
- **Build System**: Gradle 9.x with Kotlin DSL (`build.gradle.kts`)

### Cloning & Building

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/alexqr.git
   cd alexqr
   ```

2. Open the project in Android Studio or build directly using the Gradle wrapper:
   ```bash
   # Build the debug APK
   ./gradlew assembleDebug

   # Run local JVM unit tests
   ./gradlew testDebugUnitTest
   ```

3. The generated APK will be available in:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🧪 Testing

AlexQr includes comprehensive local JVM unit tests using **JUnit 4** and **Robolectric**:

- **QrCodeGenerator Tests**: Validates BitMatrix generation, logo composition, and bottom text banner calculation under `ErrorCorrectionLevel.H`.
- **ParsedQrContent Tests**: Validates regex and URL/Wi-Fi/vCard parsing logic.
- **QrStyleConfig Tests**: Validates JSON serialization and deserialization for style persistence.

Run the test suite:
```bash
./gradlew testDebugUnitTest
```

---

## 🔒 Security & Privacy

- **100% Offline**: No network sockets, HTTP clients, or analytics SDKs are included.
- **Zero Telemetry**: We do not collect crash reports, device identifiers, or location records.
- **Camera Access**: Camera hardware is accessed solely within `CameraView.kt` via CameraX for on-device barcode parsing. No frames are uploaded or saved to disk without user initiation.
- See [SECURITY.md](SECURITY.md) for our detailed security policy.

---

## 🤝 Contributing

Contributions, bug reports, and feature proposals are warmly welcomed! Please read our [CONTRIBUTING.md](CONTRIBUTING.md) guide before submitting pull requests.

Check out [DEVELOPMENT.md](DEVELOPMENT.md) for a practical step-by-step tutorial on customizing the QR generator, adding new theme palettes, or expanding barcode types.

---

## 👨‍💻 Developer & Attribution

Developed with care by **MUPOLE UWIZEYE Alexis**:
- 🌐 **Portfolio**: [alexismupole.dev](https://alexismupole.dev)
- ✉️ **Contact**: [alexismupole@gmail.com](mailto:alexismupole@gmail.com)

---

## 📄 License

This project is licensed under the **Apache License, Version 2.0**. See the [LICENSE](LICENSE) file for complete details.

```
Copyright 2026 MUPOLE UWIZEYE Alexis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
