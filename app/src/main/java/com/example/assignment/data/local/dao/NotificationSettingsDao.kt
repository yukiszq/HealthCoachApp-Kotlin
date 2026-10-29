package com.example.assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assignment.data.local.entity.NotificationSettingsEntity

@Dao
interface NotificationSettingsDao {

    @Query("SELECT * FROM notification_settings_table WHERE userId = :userId LIMIT 1")
    suspend fun getSettingsForUser(userId: Int): NotificationSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: NotificationSettingsEntity): Long
}
