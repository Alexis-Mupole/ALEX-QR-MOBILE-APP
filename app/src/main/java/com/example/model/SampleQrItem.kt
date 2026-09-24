package com.example.model

data class SampleQrItem(
    val title: String,
    val description: String,
    val type: String,
    val content: String,
    val iconName: String,
    val defaultStyle: QrStyleConfig
) {
    companion object {
        fun getPrebakedSamples(): List<SampleQrItem> {
            return listOf(
                SampleQrItem(
                    title = "Alexis Mupole Portfolio",
                    description = "Official portfolio of Alexis Mupole (creator of AlexQr)",
                    type = "URL",
                    content = "https://alexismupole.dev",
                    iconName = "link",
                    defaultStyle = QrStyleConfig(
                        primaryColor = 0xFF0F172A,
                        secondaryColor = 0xFF2563EB,
                        useGradient = true,
                        backgroundColor = 0xFFFFFFFF,
                        dotShape = DotShape.SQUARE,
                        eyeStyle = EyeStyle.SQUARE
                    )
                ),
                SampleQrItem(
                    title = "MUPOLE Alexis - vCard Contact",
                    description = "Verified professional digital business card",
                    type = "CONTACT",
                    content = "BEGIN:VCARD\nVERSION:3.0\nFN:MUPOLE UWIZEYE Alexis\nTITLE:Software Engineer\nEMAIL:contact@alexismupole.dev\nURL:https://alexismupole.dev\nTEL:+250788000000\nEND:VCARD",
                    iconName = "person",
                    defaultStyle = QrStyleConfig(
                        primaryColor = 0xFF1E1B4B,
                        secondaryColor = 0xFF4338CA,
                        useGradient = true,
                        backgroundColor = 0xFFF5F3FF,
                        dotShape = DotShape.ROUNDED,
                        eyeStyle = EyeStyle.SQUARE
                    )
                ),
                SampleQrItem(
                    title = "Secure Guest Wi-Fi",
                    description = "Instant connection payload for WPA2 local wireless network",
                    type = "WIFI",
                    content = "WIFI:S:AlexQr_HighSpeed;T:WPA;P:FastSecure2026;H:false;;",
                    iconName = "wifi",
                    defaultStyle = QrStyleConfig(
                        primaryColor = 0xFF065F46,
                        secondaryColor = 0xFF0D9488,
                        useGradient = true,
                        backgroundColor = 0xFFF0FDF4,
                        dotShape = DotShape.ROUNDED,
                        eyeStyle = EyeStyle.SQUARE
                    )
                ),
                SampleQrItem(
                    title = "Emergency Contact Hotline",
                    description = "Tap to dial standard telephone URI",
                    type = "PHONE",
                    content = "tel:+18002738255",
                    iconName = "phone",
                    defaultStyle = QrStyleConfig(
                        primaryColor = 0xFF991B1B,
                        secondaryColor = 0xFFDC2626,
                        useGradient = true,
                        backgroundColor = 0xFFFEF2F2,
                        dotShape = DotShape.SQUARE,
                        eyeStyle = EyeStyle.SQUARE
                    )
                ),
                SampleQrItem(
                    title = "Developer Feedback Email",
                    description = "Pre-composed direct email to developer",
                    type = "EMAIL",
                    content = "mailto:contact@alexismupole.dev?subject=AlexQr%20App%20Feedback&body=Hello%20Alexis,",
                    iconName = "email",
                    defaultStyle = QrStyleConfig(
                        primaryColor = 0xFF7C2D12,
                        secondaryColor = 0xFFEA580C,
                        useGradient = true,
                        backgroundColor = 0xFFFFF7ED,
                        dotShape = DotShape.ROUNDED,
                        eyeStyle = EyeStyle.SQUARE
                    )
                ),
                SampleQrItem(
                    title = "Kigali Innovation City Geo Pin",
                    description = "Geographic map coordinates with location label",
                    type = "GEO",
                    content = "geo:-1.9441,30.0619?q=-1.9441,30.0619(Kigali%20Tech%20Hub)",
                    iconName = "place",
                    defaultStyle = QrStyleConfig(
                        primaryColor = 0xFF581C87,
                        secondaryColor = 0xFF7E22CE,
                        useGradient = true,
                        backgroundColor = 0xFFFAF5FF,
                        dotShape = DotShape.SQUARE,
                        eyeStyle = EyeStyle.SQUARE
                    )
                )
            )
        }
    }
}
