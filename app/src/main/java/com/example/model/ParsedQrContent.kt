package com.example.model

import java.net.URLDecoder

data class ParsedQrContent(
    val contentType: String, // "URL", "WIFI", "PHONE", "EMAIL", "SMS", "CONTACT", "GEO", "TEXT"
    val displayTitle: String,
    val displaySubtitle: String,
    val rawContent: String,
    val url: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val smsNumber: String? = null,
    val smsBody: String? = null,
    val wifiSsid: String? = null,
    val wifiPassword: String? = null,
    val wifiAuthType: String? = null,
    val wifiHidden: Boolean = false,
    val contactName: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val geoLat: Double? = null,
    val geoLng: Double? = null
) {
    companion object {
        fun parse(raw: String): ParsedQrContent {
            val trimmed = raw.trim()

            // 1. Wi-Fi
            if (trimmed.startsWith("WIFI:", ignoreCase = true)) {
                return parseWifi(trimmed)
            }

            // 2. URL
            if (trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) ||
                trimmed.startsWith("www.", ignoreCase = true)
            ) {
                val fullUrl = if (trimmed.startsWith("www.", ignoreCase = true)) "https://$trimmed" else trimmed
                return ParsedQrContent(
                    contentType = "URL",
                    displayTitle = "Web Address",
                    displaySubtitle = fullUrl,
                    rawContent = trimmed,
                    url = fullUrl
                )
            }

            // 3. Phone (tel:)
            if (trimmed.startsWith("tel:", ignoreCase = true)) {
                val phone = trimmed.substring(4)
                return ParsedQrContent(
                    contentType = "PHONE",
                    displayTitle = "Phone Number",
                    displaySubtitle = phone,
                    rawContent = trimmed,
                    phone = phone
                )
            }

            // 4. SMS (smsto: or sms:)
            if (trimmed.startsWith("smsto:", ignoreCase = true) || trimmed.startsWith("sms:", ignoreCase = true)) {
                return parseSms(trimmed)
            }

            // 5. Email (mailto: or MATMSG:)
            if (trimmed.startsWith("mailto:", ignoreCase = true) || trimmed.startsWith("MATMSG:", ignoreCase = true)) {
                return parseEmail(trimmed)
            }

            // 6. vCard / MeCard Contact
            if (trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) || trimmed.startsWith("MECARD:", ignoreCase = true)) {
                return parseContact(trimmed)
            }

            // 7. Geo location (geo:lat,lng)
            if (trimmed.startsWith("geo:", ignoreCase = true)) {
                val coords = trimmed.substring(4).split("?").firstOrNull()?.split(",")
                val lat = coords?.getOrNull(0)?.toDoubleOrNull()
                val lng = coords?.getOrNull(1)?.toDoubleOrNull()
                return ParsedQrContent(
                    contentType = "GEO",
                    displayTitle = "Map Location",
                    displaySubtitle = "Coordinates: ${lat ?: 0.0}, ${lng ?: 0.0}",
                    rawContent = trimmed,
                    geoLat = lat,
                    geoLng = lng
                )
            }

            // Check if string looks like phone number (+123456 or digits with dashes)
            if (trimmed.matches(Regex("^[+]?[0-9\\s\\-()]{7,18}$"))) {
                return ParsedQrContent(
                    contentType = "PHONE",
                    displayTitle = "Phone Number",
                    displaySubtitle = trimmed,
                    rawContent = trimmed,
                    phone = trimmed
                )
            }

            // Check if email
            if (trimmed.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))) {
                return ParsedQrContent(
                    contentType = "EMAIL",
                    displayTitle = "Email Address",
                    displaySubtitle = trimmed,
                    rawContent = trimmed,
                    email = trimmed
                )
            }

            // Default Text
            val firstLine = trimmed.lines().firstOrNull() ?: trimmed
            val truncatedTitle = if (firstLine.length > 40) firstLine.take(37) + "..." else firstLine
            return ParsedQrContent(
                contentType = "TEXT",
                displayTitle = if (trimmed.length > 50) "Text Note" else truncatedTitle,
                displaySubtitle = if (trimmed.length > 50) truncatedTitle else "Plain text content",
                rawContent = trimmed
            )
        }

        private fun parseWifi(raw: String): ParsedQrContent {
            var ssid = ""
            var password = ""
            var auth = "WPA"
            var hidden = false

            val content = raw.removePrefix("WIFI:").removePrefix("wifi:")
            val parts = content.split(";")
            for (part in parts) {
                if (part.startsWith("S:", ignoreCase = true)) {
                    ssid = part.substring(2)
                } else if (part.startsWith("P:", ignoreCase = true)) {
                    password = part.substring(2)
                } else if (part.startsWith("T:", ignoreCase = true)) {
                    auth = part.substring(2)
                } else if (part.startsWith("H:", ignoreCase = true)) {
                    hidden = part.substring(2).equals("true", ignoreCase = true)
                }
            }

            return ParsedQrContent(
                contentType = "WIFI",
                displayTitle = "Wi-Fi Network: $ssid",
                displaySubtitle = "Security: $auth • Password: ${if (password.isNotEmpty()) "••••••••" else "None"}",
                rawContent = raw,
                wifiSsid = ssid,
                wifiPassword = password,
                wifiAuthType = auth,
                wifiHidden = hidden
            )
        }

        private fun parseSms(raw: String): ParsedQrContent {
            var number = ""
            var body = ""

            if (raw.startsWith("smsto:", ignoreCase = true)) {
                val rem = raw.substring(6)
                val split = rem.split(":", limit = 2)
                number = split[0]
                if (split.size > 1) body = split[1]
            } else if (raw.startsWith("sms:", ignoreCase = true)) {
                val rem = raw.substring(4)
                val split = rem.split("?")
                number = split[0]
                if (split.size > 1) {
                    val query = split[1]
                    val params = query.split("&")
                    for (p in params) {
                        if (p.startsWith("body=", ignoreCase = true)) {
                            body = try {
                                URLDecoder.decode(p.substring(5), "UTF-8")
                            } catch (e: Exception) {
                                p.substring(5)
                            }
                        }
                    }
                }
            }

            return ParsedQrContent(
                contentType = "SMS",
                displayTitle = "SMS to $number",
                displaySubtitle = if (body.isNotEmpty()) body else "Send SMS message",
                rawContent = raw,
                smsNumber = number,
                smsBody = body
            )
        }

        private fun parseEmail(raw: String): ParsedQrContent {
            var email = ""
            var subject = ""
            var body = ""

            if (raw.startsWith("mailto:", ignoreCase = true)) {
                val rem = raw.substring(7)
                val split = rem.split("?")
                email = split[0]
                if (split.size > 1) {
                    val params = split[1].split("&")
                    for (p in params) {
                        if (p.startsWith("subject=", ignoreCase = true)) {
                            subject = try { URLDecoder.decode(p.substring(8), "UTF-8") } catch (e: Exception) { p.substring(8) }
                        } else if (p.startsWith("body=", ignoreCase = true)) {
                            body = try { URLDecoder.decode(p.substring(5), "UTF-8") } catch (e: Exception) { p.substring(5) }
                        }
                    }
                }
            } else if (raw.startsWith("MATMSG:", ignoreCase = true)) {
                val rem = raw.removePrefix("MATMSG:")
                val parts = rem.split(";")
                for (p in parts) {
                    if (p.startsWith("TO:", ignoreCase = true)) email = p.substring(3)
                    else if (p.startsWith("SUB:", ignoreCase = true)) subject = p.substring(4)
                    else if (p.startsWith("BODY:", ignoreCase = true)) body = p.substring(5)
                }
            }

            val subtitle = when {
                subject.isNotEmpty() -> "Subject: $subject"
                email.isNotEmpty() -> email
                else -> "Compose Email"
            }

            return ParsedQrContent(
                contentType = "EMAIL",
                displayTitle = if (email.isNotEmpty()) "Email to $email" else "Email Message",
                displaySubtitle = subtitle,
                rawContent = raw,
                email = email,
                smsBody = body
            )
        }

        private fun parseContact(raw: String): ParsedQrContent {
            var name = ""
            var tel = ""
            var email = ""

            if (raw.startsWith("BEGIN:VCARD", ignoreCase = true)) {
                val lines = raw.lines()
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.startsWith("FN:", ignoreCase = true)) {
                        name = trimmed.substring(3)
                    } else if (name.isEmpty() && trimmed.startsWith("N:", ignoreCase = true)) {
                        val nParts = trimmed.substring(2).split(";")
                        name = nParts.reversed().filter { it.isNotBlank() }.joinToString(" ")
                    } else if (trimmed.startsWith("TEL", ignoreCase = true)) {
                        val idx = trimmed.indexOf(":")
                        if (idx != -1) tel = trimmed.substring(idx + 1)
                    } else if (trimmed.startsWith("EMAIL", ignoreCase = true)) {
                        val idx = trimmed.indexOf(":")
                        if (idx != -1) email = trimmed.substring(idx + 1)
                    }
                }
            } else if (raw.startsWith("MECARD:", ignoreCase = true)) {
                val rem = raw.removePrefix("MECARD:")
                val parts = rem.split(";")
                for (p in parts) {
                    if (p.startsWith("N:", ignoreCase = true)) name = p.substring(2).replace(",", " ")
                    else if (p.startsWith("TEL:", ignoreCase = true)) tel = p.substring(4)
                    else if (p.startsWith("EMAIL:", ignoreCase = true)) email = p.substring(6)
                }
            }

            val title = if (name.isNotEmpty()) "Contact: $name" else "Contact Card"
            val sub = listOfNotNull(tel.takeIf { it.isNotEmpty() }, email.takeIf { it.isNotEmpty() }).joinToString(" • ")

            return ParsedQrContent(
                contentType = "CONTACT",
                displayTitle = title,
                displaySubtitle = if (sub.isNotEmpty()) sub else "vCard contact information",
                rawContent = raw,
                contactName = name,
                contactPhone = tel,
                contactEmail = email
            )
        }
    }
}
