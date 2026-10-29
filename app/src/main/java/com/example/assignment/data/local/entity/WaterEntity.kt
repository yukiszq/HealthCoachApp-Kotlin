package com.example.assignment.data.local.entity

import androidx.room.Entity
import com.example.assignment.model.WaterRecord

/**
 * Local (Room) copy of one [WaterRecord]. Columns match the model one-to-one.
 * Primary key is (userId, waterId).
 */
@Entity(tableName = "water_table", primaryKeys = ["userId", "waterId"])
data class WaterEntity(
    val userId: Int,
    val waterId: Int,
    val intakeAmount: Int,
    val unit: String,
    val loggedAt: String
) {
    fun toModel(): WaterRecord = WaterRecord(
        waterId = waterId,
        userId = userId,
        intakeAmount = intakeAmount,
        unit = unit,
        loggedAt = loggedAt
    )

    companion object {
        fun fromModel(userId: Int, record: WaterRecord) = WaterEntity(
            userId = userId,
            waterId = record.waterId,
            intakeAmount = record.intakeAmount,
            unit = record.unit,
            loggedAt = record.loggedAt
        )
    }
}
