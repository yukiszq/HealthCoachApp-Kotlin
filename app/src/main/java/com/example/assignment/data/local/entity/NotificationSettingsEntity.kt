package com.example.assignment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment.data.UserNotificationSettings

/**
 * Local (Room) copy of one user's [UserNotificationSettings]. One row per user.
 */
@Entity(tableName = "notification_settings_table")
data class NotificationSettingsEntity(
    @PrimaryKey val userId: Int,
    val waterReminder: Boolean,
    val waterTime: String,
    val mealReminder: Boolean,
    val mealTime: String,
    val exerciseReminder: Boolean,
    val exerciseTime: String,
    val sleepReminder: Boolean,
    val sleepTime: String,
    val achievementNotification: Boolean
) {
    fun toModel(): UserNotificationSettings = UserNotificationSettings(
        waterReminder = waterReminder,
        waterTime = waterTime,
        mealReminder = mealReminder,
        mealTime = mealTime,
        exerciseReminder = exerciseReminder,
        exerciseTime = exerciseTime,
        sleepReminder = sleepReminder,
        sleepTime = sleepTime,
        achievementNotification = achievementNotification
    )

    companion object {
        fun fromModel(userId: Int, s: UserNotificationSettings) = NotificationSettingsEntity(
            userId = userId,
            waterReminder = s.waterReminder,
            waterTime = s.waterTime,
            mealReminder = s.mealReminder,
            mealTime = s.mealTime,
            exerciseReminder = s.exerciseReminder,
            exerciseTime = s.exerciseTime,
            sleepReminder = s.sleepReminder,
            sleepTime = s.sleepTime,
            achievementNotification = s.achievementNotification
        )
    }
}
