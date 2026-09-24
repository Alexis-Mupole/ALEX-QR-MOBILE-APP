# Contributing to AlexQr

Thank you for your interest in contributing to **AlexQr**! We welcome contributions from developers of all experience levels. Whether you are fixing a bug, adding new QR styling options, enhancing performance, or improving documentation, your help is appreciated.

Please take a moment to review this document to ensure smooth collaboration.

---

## 🧭 Code of Conduct

By participating in this project, you agree to abide by the [Code of Conduct](CODE_OF_CONDUCT.md). Please treat all contributors with respect and kindness.

---

## 🛠️ Development Setup

1. **Prerequisites**:
   - **Android Studio**: Ladybug (2024.2+) or later.
   - **Java Development Kit (JDK)**: JDK 17 or JDK 21.
   - **Android SDK Platform**: API Level 36 (Minimum supported: API 24).
   - **Git**: Latest stable version.

2. **Fork and Clone**:
   ```bash
   git clone https://github.com/Alexis-Mupole/ALEX-QR-MOBILE-APP.git
   cd ALEX-QR-MOBILE-APP
   ```

3. **Open in Android Studio**:
   - Select **Open an Existing Project** and navigate to the cloned directory.
   - Allow Gradle to sync dependencies.

4. **Verify the Build**:
   ```bash
   ./gradlew assembleDebug
   ./gradlew testDebugUnitTest
   ```

---

## 🌿 Branching Strategy & Git Workflow

1. Always branch off the `main` branch:
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/issue-description
   ```

2. Make clean, atomic commits with informative commit messages:
   - Use conventional commit prefixes:
     - `feat:` for new capabilities (e.g., `feat: add star-shaped QR finder eyes`)
     - `fix:` for bug fixes (e.g., `fix: prevent camera flash desync on orientation change`)
     - `perf:` for performance optimizations (e.g., `perf: cache rendered BitMatrix bitmap`)
     - `refactor:` for code restructuring without feature changes
     - `docs:` for markdown updates
     - `test:` for new unit tests

3. Ensure all tests pass before pushing:
   ```bash
   ./gradlew testDebugUnitTest
   ```

4. Push your branch and open a Pull Request (PR) against `main`.

---

## 📐 Code Style & Conventions

AlexQr is written entirely in modern Kotlin with Jetpack Compose. Please adhere to these guidelines:

### Kotlin Guidelines
- Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Use **Kotlin Coroutines** and **Flow** for asynchronous programming.
- Use unidirectional data flow: State down, events up.
- Keep source files modular and under 500 lines when practical.
- Never hardcode strings; define them in `res/values/strings.xml` or pass them via typed state.

### Jetpack Compose Guidelines
- Adhere to **Material Design 3 (M3)** standards.
- All touch targets must be at least **48.dp x 48.dp**.
- Every interactive element must supply an accessible `contentDescription`.
- Avoid hardcoded screen widths; use `BoxWithConstraints` and responsive modifiers (`fillMaxWidth()`, `widthIn(max = 600.dp)`).
- Use `remember` and `derivedStateOf` to prevent redundant recompositions.

### Privacy & Permissions Policy
- **No Internet Access**: AlexQr is designed to be 100% offline. **Do not** add `android.permission.INTERNET` or integrate external analytics/telemetry SDKs.
- Camera hardware must only be accessed when the user is actively on the Scanner screen and must be unbound when paused or navigating away.

---

## 🧪 Writing Unit Tests

When adding new features or refactoring business logic, include unit tests:
- Place unit tests in `app/src/test/java/com/example/`.
- Use Robolectric for tests requiring Android framework dependencies (such as Bitmaps, Canvas, or Intent resolution).
- Test edge cases (e.g., empty QR inputs, max character limits, malformed URLs/vCards).

---

## 🐛 Submitting Issues & Feature Requests

If you find a bug or have a suggestion:
1. Search existing issues to ensure it hasn't already been reported.
2. If opening a bug report, include:
   - Device model and Android OS version.
   - Steps to reproduce.
   - Expected vs. actual behavior.
   - Logcat output if applicable.
3. For feature requests, clearly describe the problem the feature solves and provide UI mockups or examples if possible.

Thank you for helping build a better, privacy-first QR tool!
