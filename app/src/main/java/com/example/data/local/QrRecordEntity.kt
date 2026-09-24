package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_records")
data class QrRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordType: String, // "SCANNED" or "GENERATED"
    val rawContent: String,
    val contentType: String, // "TEXT", "URL", "WIFI", "PHONE", "EMAIL", "SMS", "CONTACT"
    val stylingParameters: String = "{}", // JSON bundle of colors/shapes used
    val timestamp: Long = System.currentTimeMillis(),
    val title: String = ""
)
