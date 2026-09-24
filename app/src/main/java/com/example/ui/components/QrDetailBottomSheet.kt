package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ParsedQrContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrDetailBottomSheet(
    item: ParsedQrContent?,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onEditInDesigner: ((ParsedQrContent) -> Unit)? = null
) {
    if (item == null) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getContentTypeIcon(item.contentType),
                        contentDescription = item.contentType,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.contentType,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle Description
            if (item.displaySubtitle.isNotBlank()) {
                Text(
                    text = item.displaySubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Raw Content Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DECODED PAYLOAD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.rawContent,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Wi-Fi Specific details if applicable
            if (item.contentType == "WIFI" && !item.wifiPassword.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = {
                        copyToClipboard(context, item.wifiPassword, "Wi-Fi password copied")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Wi-Fi Password: ${item.wifiPassword}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        copyToClipboard(context, item.rawContent, "Content copied to clipboard")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_copy_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy")
                }

                FilledTonalButton(
                    onClick = {
                        shareText(context, item.rawContent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_share_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }
            }

            // Contextual Action Button (Dial, Open Link, Compose SMS, etc.)
            when (item.contentType) {
                "URL" -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            openUrl(context, item.url ?: item.rawContent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Web Link")
                    }
                }
                "PHONE" -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            dialPhone(context, item.phone ?: item.rawContent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dial Phone Number")
                    }
                }
                "EMAIL" -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            sendEmail(context, item.email ?: item.rawContent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Email")
                    }
                }
                "SMS" -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            sendSms(context, item.smsNumber ?: "", item.smsBody ?: "")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send SMS Message")
                    }
                }
                "CONTACT" -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            addContact(context, item.contactName, item.contactPhone, item.contactEmail)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Contacts")
                    }
                }
                "GEO" -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            openMap(context, item.geoLat, item.geoLng, item.rawContent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View on Map")
                    }
                }
            }

            // Edit in Designer option
            if (onEditInDesigner != null) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                            onEditInDesigner(item)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Load into QR Designer")
                }
            }
        }
    }
}

fun getContentTypeIcon(type: String) = when (type) {
    "URL" -> Icons.Default.Link
    "WIFI" -> Icons.Default.Wifi
    "PHONE" -> Icons.Default.Phone
    "EMAIL" -> Icons.Default.Email
    "SMS" -> Icons.Default.Sms
    "CONTACT" -> Icons.Default.Person
    "GEO" -> Icons.Default.NearMe
    else -> Icons.Default.TextFields
}

private fun copyToClipboard(context: Context, text: String, message: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("AlexQr Content", text))
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

private fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share decoded content"))
}

private fun openUrl(context: Context, url: String) {
    try {
        val uri = Uri.parse(if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No app available to handle this link", Toast.LENGTH_SHORT).show()
    }
}

private fun dialPhone(context: Context, phone: String) {
    try {
        val clean = phone.removePrefix("tel:")
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to launch dialer", Toast.LENGTH_SHORT).show()
    }
}

private fun sendEmail(context: Context, email: String) {
    try {
        val clean = email.removePrefix("mailto:")
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$clean"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to launch email app", Toast.LENGTH_SHORT).show()
    }
}

private fun sendSms(context: Context, number: String, body: String) {
    try {
        val clean = number.removePrefix("smsto:").removePrefix("sms:")
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$clean")).apply {
            putExtra("sms_body", body)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to launch SMS app", Toast.LENGTH_SHORT).show()
    }
}

private fun addContact(context: Context, name: String?, phone: String?, email: String?) {
    try {
        val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
            if (!name.isNullOrBlank()) putExtra(ContactsContract.Intents.Insert.NAME, name)
            if (!phone.isNullOrBlank()) putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            if (!email.isNullOrBlank()) putExtra(ContactsContract.Intents.Insert.EMAIL, email)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open contacts editor", Toast.LENGTH_SHORT).show()
    }
}

private fun openMap(context: Context, lat: Double?, lng: Double?, raw: String) {
    try {
        val uri = if (lat != null && lng != null) {
            Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        } else {
            Uri.parse(raw)
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open map application", Toast.LENGTH_SHORT).show()
    }
}
