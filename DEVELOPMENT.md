# Developer & Customization Guide

This guide provides practical, step-by-step instructions for extending and customizing **AlexQr**. Whether you want to add new QR shapes, introduce brand colors, or parse new payload schemas, follow the recipes below.

---

## 🎨 1. Adding a New QR Dot Shape

QR dot shapes are defined in `app/src/main/java/com/example/model/QrStyleConfig.kt` and rendered in `app/src/main/java/com/example/generator/QrCodeGenerator.kt`.

### Step 1: Update the Enum
Open `QrStyleConfig.kt` and add your new shape:

```kotlin
enum class DotShape(val label: String) {
    SQUARE("Square"),
    ROUNDED("Rounded"),
    CIRCLE("Dots"),
    DIAMOND("Diamond") // <-- New shape
}
```

### Step 2: Implement the Render Logic
Open `QrCodeGenerator.kt` inside the `renderStyledQrBitmap` function. In the dot-rendering loop, add handling for `DotShape.DIAMOND`:

```kotlin
when (style.dotShape) {
    DotShape.SQUARE -> {
        canvas.drawRect(left, top, right, bottom, fgPaint)
    }
    DotShape.ROUNDED -> {
        val cornerRadius = scale * 0.35f
        canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, fgPaint)
    }
    DotShape.CIRCLE -> {
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
enum class EyeStyle(val label: String) {
    SQUARE("Square"),
    ROUNDED("Rounded"),
    CIRCLE("Circle"),
    LEAF("Leaf") // <-- New style
}
```

### Step 2: Render in `drawCustomFinderEye` in `QrCodeGenerator.kt`
Add handling for your new eye style within the outer and inner eye drawing routines.

---

## 🎨 3. Adding a New App Theme Palette

App palettes are defined in `app/src/main/java/com/example/ui/theme/Color.kt`.

### Step 1: Define Your Colors
```kotlin
// Example: Cyber Teal Palette
val TealPrimary = Color(0xFF009688)
val TealOnPrimary = Color(0xFFFFFFFF)
val TealContainer = Color(0xFFE0F2F1)
val TealOnContainer = Color(0xFF004D40)
```

### Step 2: Register in `ThemePalette` Enum
```kotlin
enum class ThemePalette(val displayName: String) {
    INDIGO("Indigo"),
    EMERALD("Emerald"),
    SUNSET("Sunset"),
    PURPLE("Purple"),
    AMBER("Amber"),
    SLATE("Slate"),
    TEAL("Teal") // <-- Add new palette
}
```

### Step 3: Map in `Theme.kt`
In `app/src/main/java/com/example/ui/theme/Theme.kt`, add the mapping inside `getPaletteColors()`:

```kotlin
ThemePalette.TEAL -> if (darkTheme) {
    darkColorScheme(primary = TealPrimary, ...)
} else {
    lightColorScheme(primary = TealPrimary, ...)
}
```

---

## 📦 4. Adding a Curated Template in Samples Hub

Samples are defined in `app/src/main/java/com/example/model/SampleQrItem.kt`.

To add a new sample:
```kotlin
SampleQrItem(
    id = "sample_discord",
    title = "Discord Community",
    category = "Social",
    description = "Direct invite link to community discussion",
    samplePayload = "https://discord.gg/example",
    icon = Icons.Default.Chat,
    defaultStyle = QrStyleConfig(
        dotShape = DotShape.ROUNDED,
        eyeStyle = EyeStyle.ROUNDED,
        foregroundColor = 0xFF5865F2.toInt(),
        showBottomText = true,
        bottomText = "JOIN DISCORD"
    )
)
```

---

## 🔍 5. Adding a New Barcode Payload Parser

Payloads are detected and categorized in `app/src/main/java/com/example/model/ParsedQrContent.kt`.

### Step 1: Add Content Type
```kotlin
enum class QrContentType {
    URL, WIFI, VCARD, PHONE, EMAIL, SMS, GEO, CRYPTO, TEXT,
    CALENDAR // <-- New type
}
```

### Step 2: Add Detection Regex or Prefix
In `ParsedQrContent.fromRaw()`:
```kotlin
when {
    raw.startsWith("BEGIN:VEVENT", ignoreCase = true) -> {
        // Parse calendar event summary, start time, end time
        ParsedQrContent(
            raw = raw,
            type = QrContentType.CALENDAR,
            title = "Calendar Event",
            details = mapOf("Summary" to eventTitle, "Date" to eventDate)
        )
    }
    // ...
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
   Room.databaseBuilder(context, AppDatabase::class.java, "alex_qr_database")
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
