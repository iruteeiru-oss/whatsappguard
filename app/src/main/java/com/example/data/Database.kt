package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatLogDao {
    @Query("SELECT * FROM chat_logs ORDER BY timestamp DESC")
    fun getAllChatLogs(): Flow<List<ChatLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatLog(chatLog: ChatLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatLogs(chatLogs: List<ChatLog>)

    @Query("DELETE FROM chat_logs")
    suspend fun clearAllChatLogs()
}

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items ORDER BY timestamp DESC")
    fun getAllMediaItems(): Flow<List<MediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(mediaItems: List<MediaItem>)

    @Update
    suspend fun updateMediaItem(mediaItem: MediaItem)

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getMediaItemById(id: Int): MediaItem?

    @Query("DELETE FROM media_items")
    suspend fun clearAllMediaItems()
}

@Dao
interface DeviceStatusDao {
    @Query("SELECT * FROM device_status WHERE id = 1")
    fun getDeviceStatusFlow(): Flow<DeviceStatus?>

    @Query("SELECT * FROM device_status WHERE id = 1")
    suspend fun getDeviceStatus(): DeviceStatus?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDeviceStatus(deviceStatus: DeviceStatus)
}

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLogItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLogItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLogs(callLogs: List<CallLogItem>)

    @Query("DELETE FROM call_logs")
    suspend fun clearAllCallLogs()
}

@Database(entities = [ChatLog::class, MediaItem::class, DeviceStatus::class, CallLogItem::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatLogDao(): ChatLogDao
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun deviceStatusDao(): DeviceStatusDao
    abstract fun callLogDao(): CallLogDao
}
