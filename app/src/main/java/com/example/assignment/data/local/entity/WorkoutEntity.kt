package com.example.assignment.data.local.entity

import androidx.room.Entity
import com.example.assignment.model.WorkoutItem

/**
 * Local (Room) copy of one [WorkoutItem]. Columns match the model one-to-one,
 * plus [userId]. The workout id is a String ("1", "2", ...) generated per user,
 * so the primary key is the pair (userId, id).
 */
@Entity(tableName = "workout_table", primaryKeys = ["userId", "id"])
data class WorkoutEntity(
    val userId: Int,
    val id: String,
    val title: String,
    val time: String,
    val duration: String,
    val calories: Int,
    val distanceKm: Double?,
    val date: String,
    val notes: String
) {
    fun toModel(): WorkoutItem = WorkoutItem(
        id = id,
        title = title,
        time = time,
        duration = duration,
        calories = calories,
        distanceKm = distanceKm,
        date = date,
        notes = notes
    )

    companion object {
        fun fromModel(userId: Int, workout: WorkoutItem) = WorkoutEntity(
            userId = userId,
            id = workout.id,
            title = workout.title,
            time = workout.time,
            duration = workout.duration,
            calories = workout.calories,
            distanceKm = workout.distanceKm,
            date = workout.date,
            notes = workout.notes
        )
    }
}
