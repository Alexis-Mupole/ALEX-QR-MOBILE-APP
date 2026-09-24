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
  - Added tap-to-focus and pinch-to-zoom gestures.
  - Added animated laser reticle overlay for targeting.
  - Added battery-conscious lifecycle safety (camera automatically unbinds when inactive).
- **Custom QR Code Designer Studio**:
  - Refactored generator to ZXing `BitMatrix` using `ErrorCorrectionLevel.H` (30% error tolerance).
  - Dot geometry options: Square, Rounded Rectangles, and Circular Dots.
  - Eye corner geometry options: Square, Rounded, and Circle.
  - Color styling: Foreground and background color pickers with curated palette presets and custom hex support.
  - Center logo placement: Built-in icons (Star, Heart, QR Shield, Link, Security Shield) and photo picker for custom gallery images.
  - Bottom call-to-action text banner (e.g. *"SCAN ME"*, *"PAY HERE"*, *"CONNECT TO WIFI"*) with custom background and text color controls.
  - Anti-aliased high-DPI Bitmap rendering.
  - Direct native sharing via Android `FileProvider` and direct local device saving.
- **Intelligent Parser & Action Dispatcher**:
  - URLs, Wi-Fi credentials, vCards / MeCards, SMS, Phone dialer, Email, Geo coordinates, and Cryptocurrencies (Bitcoin & Ethereum).
- **Samples Hub**:
  - Pre-configured, one-tap templates for Wi-Fi, vCards, Developer Portfolio, Crypto addresses, and SMS dispatch.
- **Local Persistence & History**:
  - Android Room SQLite local database persistence with reactive `Flow` queries.
  - Filterable by Scanned vs. Generated codes.
  - Bookmark favorites for quick access.
  - Real-time search by text payload and metadata.
  - CSV export and bulk history wipe.
- **App Personalization & Settings**:
  - 6 Material 3 color themes (Indigo Cyber, Emerald Forest, Sunset Crimson, Royal Purple, Amber Gold, Slate Minimal).
  - Theme mode options: Dark Mode, Light Mode, and Follow System.
  - Power-Saver Mode: Limits frame rate analysis and disables heavy animations to conserve battery.
  - First-time user onboarding tour with *"Do not show again"* option.
- **About Developer Screen**:
  - Dedicated profile honoring MUPOLE UWIZEYE Alexis ([alexismupole.dev](https://alexismupole.dev)).
- **Documentation Suite**:
  - Created comprehensive `README.md`, `CONTRIBUTING.md`, `ARCHITECTURE.md`, `DEVELOPMENT.md`, `SECURITY.md`, `CODE_OF_CONDUCT.md`, and `LICENSE`.
- **Open Source Clean-up**:
  - Removed all artificial intelligence boilerplate and non-relevant dependencies.
  - Enforced 100% offline privacy with zero network permissions.
