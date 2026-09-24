# Developer & Customization Guide

This guide provides practical, step-by-step instructions for extending and customizing **AlexQr**. Whether you want to add new QR shapes, introduce brand colors, or parse new payload schemas, follow the recipes below.

---

## 🎨 1. Adding a New QR Dot Shape

QR dot shapes are defined in `app/src/main/java/com/example/model/QrStyleConfig.kt` and rendered in `app/src/main/java/com/example/generator/QrCodeGenerator.kt`.

### Step 1: Update the Enum
Open `QrStyleConfig.kt` and add your new shape:

```kotlin
enum class DotShape {
    SQUARE,
    ROUNDED,
    CIRCLES,
    DIAMOND // <-- New shape
}
```

### Step 2: Implement the Render Logic
Open `QrCodeGenerator.kt` inside the `renderStyledBitmap` function. In the dot-rendering loop, add handling for `DotShape.DIAMOND`:

```kotlin
when (style.dotShape) {
    DotShape.SQUARE -> {
        canvas.drawRect(left, top, right, bottom, fgPaint)
    }
    DotShape.ROUNDED -> {
        val cornerRadius = scale * 0.35f
        canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, fgPaint)
    }
    DotShape.CIRCLES -> {
        val radius = (scale * 0.85f) / 2f
        canvas.drawCircle(cx, cy, radius, fgPaint)
    }
    DotShape.DIAMOND -> { // <-- New render logic
        val path = Path().apply {
            moveTo(cx, top)
            lineTo(right, cy)
            lineTo(cx, bottom)
            lineTo(left, cy)
            close()
        }
        canvas.drawPath(path, fgPaint)
    }
}
```

---

## 👁️ 2. Adding a New Eye Style

### Step 1: Update the Enum in `QrStyleConfig.kt`
```kotlin
enum class EyeStyle {
    SQUARE,
    ROUNDED,
    CIRCLES,
    LEAF // <-- New style
}
```

### Step 2: Render in the finder-eye branch of `renderStyledBitmap` in `QrCodeGenerator.kt`
Add handling for your new eye style within the outer and inner eye drawing routines.

---

## 🎨 3. Adding a New App Theme Palette

App palettes are defined by the `ThemePalette` enum in `app/src/main/java/com/example/data/PreferencesManager.kt`.

### Step 1: Define Your Colors
```kotlin
// Example: Cyber Teal Palette (ARGB Long values)
TEAL("Cyber Teal", 0xFF009688, 0xFF00796B),
```

### Step 2: Register in `ThemePalette` Enum
```kotlin
enum class ThemePalette(val displayName: String, val primaryColor: Long, val secondaryColor: Long) {
    ELECTRIC_BLUE("Electric Blue", 0xFF2563EB, 0xFF1D4ED8),
    EMERALD_GREEN("Emerald Green", 0xFF059669, 0xFF047857),
    ROYAL_PURPLE("Royal Purple", 0xFF7C3AED, 0xFF6D28D9),
    SUNSET_ORANGE("Sunset Orange", 0xFFEA580C, 0xFFC2410C),
    ROSE_PINK("Rose Pink", 0xFFE11D48, 0xFFBE123C),
    CYBER_CYAN("Cyber Cyan", 0xFF0891B2, 0xFF0E7490),
    DEEP_SLATE("Deep Slate", 0xFF334155, 0xFF1E293B),
    TEAL("Cyber Teal", 0xFF009688, 0xFF00796B) // <-- Add new palette
}
```

### Step 3: Use it in `Theme.kt`
`app/src/main/java/com/example/ui/theme/Theme.kt` builds the Material 3 `ColorScheme` automatically inside `buildDynamicColorScheme()` from the palette's `primaryColor` and `secondaryColor`, so no per-palette mapping is required. Simply select the palette via `AlexQrTheme(palette = ThemePalette.TEAL)`.

---

## 📦 4. Adding a Curated Template in Samples Hub

Samples are defined in `app/src/main/java/com/example/model/SampleQrItem.kt`.

To add a new sample:
```kotlin
SampleQrItem(
    title = "Discord Community",
    description = "Direct invite link to community discussion",
    type = "URL",
    content = "https://discord.gg/example",
    iconName = "link",
    defaultStyle = QrStyleConfig(
        primaryColor = 0xFF5865F2,
        secondaryColor = 0xFF4752C4,
        useGradient = true,
        backgroundColor = 0xFFFFFFFF,
        dotShape = DotShape.ROUNDED,
        eyeStyle = EyeStyle.ROUNDED,
        showBottomText = true,
        bottomText = "JOIN DISCORD"
    )
)
```

---

## 🔍 5. Adding a New Barcode Payload Parser

Payloads are detected and categorized in `app/src/main/java/com/example/model/ParsedQrContent.kt`.

### Step 1: Choose a Content Type Label
Content types are plain `String` values on `ParsedQrContent` (e.g. `"URL"`, `"WIFI"`, `"CONTACT"`, `"SMS"`, `"EMAIL"`, `"GEO"`, `"PHONE"`, `"TEXT"`). Add your own label, such as `"CALENDAR"`.

### Step 2: Add Detection Prefix
In `ParsedQrContent.parse()`:
```kotlin
if (trimmed.startsWith("BEGIN:VEVENT", ignoreCase = true)) {
    // Parse calendar event summary, start time, end time
    return ParsedQrContent(
        contentType = "CALENDAR",
        displayTitle = "Calendar Event",
        displaySubtitle = eventSummary,
        rawContent = trimmed
    )
}
```

### Step 3: Add Intent Action in `QrDetailBottomSheet.kt`
Add an action button that fires `Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)`.

---

## 💾 6. Room Database Migrations

If modifying the columns in `QrRecordEntity.kt`:
1. Increment the database version in `AppDatabase.kt`:
   ```kotlin
   @Database(entities = [QrRecordEntity::class], version = 2, exportSchema = false)
   ```
2. Define a migration or configure destructive migration for testing:
   ```kotlin
   Room.databaseBuilder(context, AppDatabase::class.java, "alexqr_database")
       .addMigrations(MIGRATION_1_2)
       .build()
   ```

---

## 🧪 7. Running & Writing Unit Tests

To run all unit tests from the terminal:
```bash
./gradlew testDebugUnitTest
```

To add a test for a new parser or renderer, create or edit files in `app/src/test/java/com/example/`.
