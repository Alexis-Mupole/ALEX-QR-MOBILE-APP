# AlexQr Architecture Guide

This document describes the architectural design, core modules, data flows, and technical decisions behind **AlexQr**.

---

## 🏛️ High-Level Architectural Pattern

AlexQr follows Android's recommended **Clean Architecture** combined with **MVVM (Model-View-ViewModel)** and **Unidirectional Data Flow (UDF)**:

```
+-----------------------------------------------------------+
|                      UI Layer (Compose)                   |
|  HomeScreen | ScannerScreen | DesignerScreen | History... |
+-----------------------------------------------------------+
                             ▲ |
             StateFlow emits | | Events / User Actions
                             | ▼
+-----------------------------------------------------------+
|                  ViewModel Layer                          |
|                  MainViewModel                            |
+-----------------------------------------------------------+
         |                                 |
         ▼                                 ▼
+-----------------------+       +---------------------------+
|    Generation &       |       |       Data Layer          |
|    Scanning Engines   |       |   QrRepository            |
| - QrCodeGenerator     |       |   AppDatabase (Room)      |
| - CameraView (ML Kit) |       |   PreferencesManager      |
+-----------------------+       +---------------------------+
```

---

## 🧩 Core Architectural Components

### 1. Presentation Layer (`com.example.ui`)
The UI is 100% declarative, implemented using **Jetpack Compose** and **Material 3**:
- **`MainActivity.kt`**: Single-activity entry point. Enables edge-to-edge layout via `enableEdgeToEdge()` and hosts the responsive navigation wrapper.
- **`AppResponsiveScaffold.kt`**: Adapts navigation for various form factors:
  - Mobile compact: Top App Bar with hamburger menu opening a `ModalNavigationDrawer`, and a bottom navigation bar.
  - Tablets / Foldables: Employs wide layouts with accessible touch targets.
- **Screens**:
  - `HomeScreen.kt`: Production dashboard featuring live statistics, quick scan/design action cards, curated feature badges, and privacy certification.
  - `ScannerScreen.kt`: Camera viewfinder with hardware flash toggle, pinch zoom, tap focus, and bottom sheet action inspector.
  - `DesignerScreen.kt`: Real-time studio for crafting custom QR codes with shapes, eye styles, palettes, logo insertion, and call-to-action text banners.
  - `SamplesHubScreen.kt`: Quick-load library with pre-configured templates (Wi-Fi, vCard, Crypto, URLs).
  - `HistoryScreen.kt`: Searchable and filterable history log backed by Room SQLite.
  - `SettingsScreen.kt`: App personalization (6 color palettes, dark/light theme, power saver mode, storage wipe).
  - `AboutDeveloperScreen.kt`: Dedicated portfolio page honoring Alexis Mupole.

---

### 2. State & Business Logic (`com.example.viewmodel.MainViewModel`)
- **Single Source of UI State**: The `MainViewModel` exposes reactive `StateFlow` streams collected safely using `collectAsStateWithLifecycle()` in composables.
- **Key Flows**:
  - `historyList`: Emits reactive list of `QrRecordEntity` objects from Room.
  - `selectedPalette`: Emits current `ThemePalette` (Indigo, Emerald, Sunset, etc.).
  - `darkThemeConfig`: Emits theme mode (`SYSTEM`, `DARK`, `LIGHT`).
  - `powerSaverMode`: Boolean toggle controlling frame rate throttling and animation suppression.
  - `styleConfig`: Live configuration object representing the active QR design state.
  - `lastScanResult`: Most recently decoded barcode payload.

---

### 3. QR Generation Engine (`com.example.generator.QrCodeGenerator`)
The generator transforms raw text into a styled, high-scannability Android `Bitmap`:
1. **BitMatrix Encoding**: Uses ZXing's `MultiFormatWriter` with `ErrorCorrectionLevel.H` (30% error resilience).
2. **Finder Pattern Identification**: Identifies the three 7x7 corner eye patterns (Top-Left, Top-Right, Bottom-Left) and reserves them from dot styling.
3. **Dot Matrix Drawing**:
   - Iterates through the matrix cells.
   - Applies the selected `DotShape` (`SQUARE`, `ROUNDED`, or `CIRCLE`).
   - Anti-aliased paints render smoothly on high-DPI displays.
4. **Eye Styling**:
   - Renders inner and outer finder eyes according to `EyeStyle` (`SQUARE`, `ROUNDED`, `CIRCLE`).
5. **Center Logo Composition**:
   - Calculates the center 20% area of the matrix.
   - Clears matrix modules behind the logo with a smooth rounded background plate.
   - Draws vector icons or custom picked bitmaps centered with crisp scaling.
6. **Bottom Call-to-Action Banner**:
   - If enabled, expands the canvas vertically.
   - Renders a pill-shaped banner with custom background color and centered typography (e.g., *"SCAN ME"*).

---

### 4. Scanner Engine (`com.example.scanner.CameraView`)
- **CameraX + ML Kit Integration**: Uses CameraX's `ImageAnalysis.Builder` connected to Google ML Kit's `BarcodeScanning.getClient()`.
- **Target Formats**: Strictly configured for `Barcode.FORMAT_QR_CODE` and 2D barcodes for maximum throughput.
- **Power Saver Throttling**: When power-saver mode is active, the analyzer introduces a frame skip delay (150ms) to reduce CPU cycles and battery consumption.
- **Lifecycle Bound**: The camera lifecycle is bound to the local `LocalLifecycleOwner`. Unbinding occurs immediately on disposal to prevent background camera hold.

---

### 5. Data Persistence Layer (`com.example.data`)
- **Room Database (`AppDatabase.kt`)**: Local SQLite database storing scan and generation events with zero cloud exposure.
- **`QrRecordEntity.kt`**:
  - `id`: Auto-incrementing primary key.
  - `content`: Raw text / payload.
  - `type`: `SCAN` or `GENERATE`.
  - `contentType`: Semantic type (`URL`, `WIFI`, `VCARD`, `TEXT`, `CRYPTO`, etc.).
  - `timestamp`: Long Unix epoch time.
  - `isFavorite`: Boolean bookmark flag.
  - `styleConfigJson`: Serialized styling parameters.
- **`PreferencesManager.kt`**: Backed by Android `SharedPreferences` for fast synchronous retrieval of user settings (theme palette, dark mode preference, power saver flag, onboarding completion).

---

## ⚡ Performance & Battery Optimization

1. **Error Correction Level H**: Guarantees fast, robust decoding under glare, low light, and physical print wear.
2. **Power-Saver Mode**:
   - Halves the frequency of ML Kit image analysis frames.
   - Stops continuous laser scan sweep animations.
3. **Bitmap Recycling**: Generator bitmaps are allocated cleanly in memory and shared via Android `FileProvider` without memory leaks.
4. **Offline Zero-Network Guarantee**: Eliminates battery drain caused by network polling, analytics beacons, and advertising SDKs.
