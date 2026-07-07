package com.example.data

import kotlinx.coroutines.flow.Flow

class MonitorRepository(
    private val chatLogDao: ChatLogDao,
    private val mediaItemDao: MediaItemDao,
    private val deviceStatusDao: DeviceStatusDao
) {
    val allChatLogs: Flow<List<ChatLog>> = chatLogDao.getAllChatLogs()
    val allMediaItems: Flow<List<MediaItem>> = mediaItemDao.getAllMediaItems()
    val deviceStatusFlow: Flow<DeviceStatus?> = deviceStatusDao.getDeviceStatusFlow()

    suspend fun insertChatLog(chatLog: ChatLog) = chatLogDao.insertChatLog(chatLog)

    suspend fun insertChatLogs(chatLogs: List<ChatLog>) = chatLogDao.insertChatLogs(chatLogs)

    suspend fun clearAllChatLogs() = chatLogDao.clearAllChatLogs()

    suspend fun insertMediaItem(mediaItem: MediaItem) = mediaItemDao.insertMediaItem(mediaItem)

    suspend fun insertMediaItems(mediaItems: List<MediaItem>) = mediaItemDao.insertMediaItems(mediaItems)

    suspend fun updateMediaItem(mediaItem: MediaItem) = mediaItemDao.updateMediaItem(mediaItem)

    suspend fun getMediaItemById(id: Int): MediaItem? = mediaItemDao.getMediaItemById(id)

    suspend fun clearAllMediaItems() = mediaItemDao.clearAllMediaItems()

    suspend fun getDeviceStatus(): DeviceStatus? = deviceStatusDao.getDeviceStatus()

    suspend fun insertOrUpdateDeviceStatus(deviceStatus: DeviceStatus) =
        deviceStatusDao.insertOrUpdateDeviceStatus(deviceStatus)

    suspend fun prepopulateIfEmpty() {
        // Prepopulate Status
        if (deviceStatusDao.getDeviceStatus() == null) {
            deviceStatusDao.insertOrUpdateDeviceStatus(
                DeviceStatus(
                    isOnline = true,
                    batteryPercentage = 78,
                    isCharging = false,
                    locationLatitude = 37.7749,
                    locationLongitude = -122.4194,
                    stealthModeEnabled = false
                )
            )
        }

        // We can check if we have chat logs, if not, prepopulate some beautiful examples
        // But let's let ViewModel control it so it's reactive or do it directly.
    }
}
