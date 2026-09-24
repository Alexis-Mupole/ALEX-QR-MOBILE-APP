# Changelog

All notable changes to **AlexQr** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-09-24

### Initial Production & Open Source Release

#### Added
- **Core Real-Time Scanner**:
  - Integrated Google ML Kit Barcode Scanning with CameraX.
  - Added hardware flash/torch toggle.
  - Added on-device scan-from-gallery-image support.
  - Added animated laser reticle overlay for targeting.
  - Added battery-conscious lifecycle safety (camera automatically unbinds when inactive).
- **Custom QR Code Designer Studio**:
  - Refactored generator to ZXing `BitMatrix` using `ErrorCorrectionLevel.H` (30% error tolerance).
  - Dot geometry options: Square, Rounded Rectangles, and Circular Dots.
  - Eye corner geometry options: Square, Rounded, and Circle.
  - Color styling: Foreground and background color pickers with curated palette presets and custom hex support.
  - Center logo placement: Built-in icons (Star, Heart, Link, Wi-Fi, User, AlexQr logo) and photo picker for custom gallery images.
  - Bottom call-to-action text banner (e.g. *"SCAN ME"*, *"PAY HERE"*, *"CONNECT TO WIFI"*) with custom background and text color controls.
  - Anti-aliased high-DPI Bitmap rendering.
  - Direct native sharing via Android `FileProvider` and direct local device saving.
- **Intelligent Parser & Action Dispatcher**:
  - URLs, Wi-Fi credentials, vCards / MeCards, SMS, Phone dialer, Email, and Geo coordinates.
- **Samples Hub**:
  - Pre-configured, one-tap templates for URLs, vCards, Wi-Fi, Phone, Email, and Geo locations.
- **Local Persistence & History**:
  - Android Room SQLite local database persistence with reactive `Flow` queries.
  - Filterable by Scanned vs. Generated codes.
  - Real-time search by text payload and metadata.
  - Native share of saved codes and bulk history wipe.
- **App Personalization & Settings**:
  - 7 Material 3 color themes (Electric Blue, Emerald Green, Royal Purple, Sunset Orange, Rose Pink, Cyber Cyan, Deep Slate).
  - Theme mode options: System, Light, Dark, and AMOLED.
  - Power-Saver Mode: Limits frame rate analysis and disables heavy animations to conserve battery.
  - First-time user onboarding tour with *"Do not show again"* option.
- **About Developer Screen**:
  - Dedicated profile honoring MUPOLE UWIZEYE Alexis ([alexismupole.dev](https://alexismupole.dev)).
- **Documentation Suite**:
  - Created comprehensive `README.md`, `CONTRIBUTING.md`, `ARCHITECTURE.md`, `DEVELOPMENT.md`, `SECURITY.md`, `CODE_OF_CONDUCT.md`, and `LICENSE`.
- **Open Source Clean-up**:
  - Removed all artificial intelligence boilerplate and non-relevant dependencies.
  - Enforced 100% offline privacy with zero network permissions.
