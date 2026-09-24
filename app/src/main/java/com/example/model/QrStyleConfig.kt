package com.example.model

import org.json.JSONObject

enum class DotShape {
    SQUARE,
    ROUNDED,
    CIRCLES
}

enum class EyeStyle {
    SQUARE,
    ROUNDED,
    CIRCLES
}

enum class CenterLogoType {
    NONE,
    ALEX_QR,
    LINK,
    WIFI,
    HEART,
    STAR,
    USER,
    CUSTOM
}

data class QrStyleConfig(
    val primaryColor: Long = 0xFF0A0E17,
    val secondaryColor: Long = 0xFF1E3A8A,
    val useGradient: Boolean = false,
    val backgroundColor: Long = 0xFFFFFFFF,
    val dotShape: DotShape = DotShape.SQUARE,
    val eyeStyle: EyeStyle = EyeStyle.SQUARE,
    val centerLogo: CenterLogoType = CenterLogoType.NONE,
    val customLogoUri: String? = null,
    val showBottomText: Boolean = false,
    val bottomText: String = "SCAN ME",
    val errorCorrectionLevel: String = "H"
) {
    fun toJson(): String {
        val obj = JSONObject()
        obj.put("primaryColor", primaryColor)
        obj.put("secondaryColor", secondaryColor)
        obj.put("useGradient", useGradient)
        obj.put("backgroundColor", backgroundColor)
        obj.put("dotShape", dotShape.name)
        obj.put("eyeStyle", eyeStyle.name)
        obj.put("centerLogo", centerLogo.name)
        if (customLogoUri != null) {
            obj.put("customLogoUri", customLogoUri)
        }
        obj.put("showBottomText", showBottomText)
        obj.put("bottomText", bottomText)
        obj.put("errorCorrectionLevel", errorCorrectionLevel)
        return obj.toString()
    }

    companion object {
        fun fromJson(jsonStr: String?): QrStyleConfig {
            if (jsonStr.isNullOrBlank()) return QrStyleConfig()
            return try {
                val obj = JSONObject(jsonStr)
                QrStyleConfig(
                    primaryColor = obj.optLong("primaryColor", 0xFF0A0E17),
                    secondaryColor = obj.optLong("secondaryColor", 0xFF1E3A8A),
                    useGradient = obj.optBoolean("useGradient", false),
                    backgroundColor = obj.optLong("backgroundColor", 0xFFFFFFFF),
                    dotShape = try {
                        DotShape.valueOf(obj.optString("dotShape", DotShape.SQUARE.name))
                    } catch (e: Exception) {
                        DotShape.SQUARE
                    },
                    eyeStyle = try {
                        EyeStyle.valueOf(obj.optString("eyeStyle", EyeStyle.SQUARE.name))
                    } catch (e: Exception) {
                        EyeStyle.SQUARE
                    },
                    centerLogo = try {
                        CenterLogoType.valueOf(obj.optString("centerLogo", CenterLogoType.NONE.name))
                    } catch (e: Exception) {
                        CenterLogoType.NONE
                    },
                    customLogoUri = if (obj.has("customLogoUri")) obj.getString("customLogoUri") else null,
                    showBottomText = obj.optBoolean("showBottomText", false),
                    bottomText = obj.optString("bottomText", "SCAN ME"),
                    errorCorrectionLevel = obj.optString("errorCorrectionLevel", "H")
                )
            } catch (e: Exception) {
                QrStyleConfig()
            }
        }
    }
}
