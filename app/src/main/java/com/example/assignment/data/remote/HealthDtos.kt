package com.example.assignment.data.remote

import com.example.assignment.data.UserAccount
import com.example.assignment.data.UserHealthGoals
import com.example.assignment.data.UserNotificationSettings
import com.example.assignment.model.MealEntry
import com.example.assignment.model.SleepRecord
import com.example.assignment.model.WaterRecord
import com.example.assignment.model.WorkoutItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * These @Serializable classes are the exact shape of the rows in each Supabase
 * table (Practical 9 uses the same idea with its `User` data class). Column
 * names in Postgres are snake_case, so @SerialName maps them to camelCase in
 * Kotlin. Each row also carries user_id so one table holds every user's data.
 */

@Serializable
data class ProfileDto(
    val id: Int,
    @SerialName("full_name") val fullName: String,
    val email: String,
    @SerialName("password") val password: String,
    val age: Int,
    val gender: String,
    val height: Double,
    val weight: Double,
    @SerialName("profile_image_uri") val profileImageUri: String,
    @SerialName("profile_setup_completed") val profileSetupCompleted: Boolean
) {
    fun toModel() = UserAccount(
        id, fullName, email, password, age, gender, height, weight,
        profileImageUri, profileSetupCompleted
    )

    companion object {
        fun fromModel(u: UserAccount) = ProfileDto(
            id = u.id, fullName = u.fullName, email = u.email, password = u.password,
            age = u.age, gender = u.gender, height = u.height, weight = u.weight,
            profileImageUri = u.profileImageUri,
            profileSetupCompleted = u.profileSetupCompleted
        )
    }
}

@Serializable
data class MealDto(
    @SerialName("user_id") val userId: Int,
    val id: Int,
    @SerialName("meal_type") val mealType: String,
    @SerialName("food_name") val foodName: String,
    val kcal: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int,
    val time: String,
    val date: String,
    val notes: String,
    @SerialName("image_uri") val imageUri: String
) {
    fun toModel() = MealEntry(id, mealType, foodName, kcal, protein, carbs, fat, fiber, time, date, notes, imageUri)

    companion object {
        fun fromModel(userId: Int, m: MealEntry) =
            MealDto(userId, m.id, m.mealType, m.foodName, m.kcal, m.protein, m.carbs, m.fat, m.fiber, m.time, m.date, m.notes, m.imageUri)
    }
}

@Serializable
data class WorkoutDto(
    @SerialName("user_id") val userId: Int,
    val id: String,
    val title: String,
    val time: String,
    val duration: String,
    val calories: Int,
    @SerialName("distance_km") val distanceKm: Double? = null,
    val date: String,
    val notes: String = ""
) {
    fun toModel() = WorkoutItem(id, title, time, duration, calories, distanceKm, date, notes)

    companion object {
        fun fromModel(userId: Int, w: WorkoutItem) =
            WorkoutDto(userId, w.id, w.title, w.time, w.duration, w.calories, w.distanceKm, w.date, w.notes)
    }
}

@Serializable
data class SleepDto(
    @SerialName("user_id") val userId: Int,
    val id: Int,
    val date: String,
    @SerialName("sleep_type") val sleepType: String,
    val bedtime: String,
    @SerialName("wake_up_time") val wakeUpTime: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    val quality: Int,
    val notes: String
) {
    fun toModel() = SleepRecord(id, date, sleepType, bedtime, wakeUpTime, durationMinutes, quality, notes)

    companion object {
        fun fromModel(userId: Int, s: SleepRecord) =
            SleepDto(userId, s.id, s.date, s.sleepType, s.bedtime, s.wakeUpTime, s.durationMinutes, s.quality, s.notes)
    }
}

@Serializable
data class WaterDto(
    @SerialName("user_id") val userId: Int,
    @SerialName("water_id") val waterId: Int,
    @SerialName("intake_amount") val intakeAmount: Int,
    val unit: String,
    @SerialName("logged_at") val loggedAt: String
) {
    fun toModel() = WaterRecord(waterId, userId, intakeAmount, unit, loggedAt)

    companion object {
        fun fromModel(userId: Int, r: WaterRecord) =
            WaterDto(userId, r.waterId, r.intakeAmount, r.unit, r.loggedAt)
    }
}

@Serializable
data class GoalsDto(
    @SerialName("user_id") val userId: Int,
    @SerialName("water_goal") val waterGoal: Int,
    @SerialName("meal_goal") val mealGoal: Int,
    @SerialName("exercise_goal") val exerciseGoal: Int,
    @SerialName("sleep_goal") val sleepGoal: Int
) {
    fun toModel() = UserHealthGoals(waterGoal, mealGoal, exerciseGoal, sleepGoal)

    companion object {
        fun fromModel(userId: Int, g: UserHealthGoals) =
            GoalsDto(userId, g.waterGoal, g.mealGoal, g.exerciseGoal, g.sleepGoal)
    }
}

@Serializable
data class NotificationSettingsDto(
    @SerialName("user_id") val userId: Int,
    @SerialName("water_reminder") val waterReminder: Boolean,
    @SerialName("water_time") val waterTime: String,
    @SerialName("meal_reminder") val mealReminder: Boolean,
    @SerialName("meal_time") val mealTime: String,
    @SerialName("exercise_reminder") val exerciseReminder: Boolean,
    @SerialName("exercise_time") val exerciseTime: String,
    @SerialName("sleep_reminder") val sleepReminder: Boolean,
    @SerialName("sleep_time") val sleepTime: String,
    @SerialName("achievement_notification") val achievementNotification: Boolean
) {
    fun toModel() = UserNotificationSettings(
        waterReminder, waterTime, mealReminder, mealTime, exerciseReminder,
        exerciseTime, sleepReminder, sleepTime, achievementNotification
    )

    companion object {
        fun fromModel(userId: Int, s: UserNotificationSettings) = NotificationSettingsDto(
            userId, s.waterReminder, s.waterTime, s.mealReminder, s.mealTime,
            s.exerciseReminder, s.exerciseTime, s.sleepReminder, s.sleepTime,
            s.achievementNotification
        )
    }
}
