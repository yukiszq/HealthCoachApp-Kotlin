package com.example.assignment.data.local.entity

import androidx.room.Entity
import com.example.assignment.model.SleepRecord

/**
 * Local (Room) copy of one [SleepRecord]. Columns match the model one-to-one,
 * plus [userId]. Primary key is (userId, id).
 */
@Entity(tableName = "sleep_table", primaryKeys = ["userId", "id"])
data class SleepEntity(
    val userId: Int,
    val id: Int,
    val date: String,
    val sleepType: String,
    val bedtime: String,
    val wakeUpTime: String,
    val durationMinutes: Int,
    val quality: Int,
    val notes: String
) {
    fun toModel(): SleepRecord = SleepRecord(
        id = id,
        date = date,
        sleepType = sleepType,
        bedtime = bedtime,
        wakeUpTime = wakeUpTime,
        durationMinutes = durationMinutes,
        quality = quality,
        notes = notes
    )

    companion object {
        fun fromModel(userId: Int, record: SleepRecord) = SleepEntity(
            userId = userId,
            id = record.id,
            date = record.date,
            sleepType = record.sleepType,
            bedtime = record.bedtime,
            wakeUpTime = record.wakeUpTime,
            durationMinutes = record.durationMinutes,
            quality = record.quality,
            notes = record.notes
        )
    }
}
