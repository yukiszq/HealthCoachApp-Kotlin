package com.example.assignment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment.data.UserHealthGoals

/**
 * Local (Room) copy of one user's [UserHealthGoals]. One row per user, so the
 * user id is the primary key.
 */
@Entity(tableName = "goals_table")
data class GoalsEntity(
    @PrimaryKey val userId: Int,
    val waterGoal: Int,
    val mealGoal: Int,
    val exerciseGoal: Int,
    val sleepGoal: Int
) {
    fun toModel(): UserHealthGoals = UserHealthGoals(
        waterGoal = waterGoal,
        mealGoal = mealGoal,
        exerciseGoal = exerciseGoal,
        sleepGoal = sleepGoal
    )

    companion object {
        fun fromModel(userId: Int, goals: UserHealthGoals) = GoalsEntity(
            userId = userId,
            waterGoal = goals.waterGoal,
            mealGoal = goals.mealGoal,
            exerciseGoal = goals.exerciseGoal,
            sleepGoal = goals.sleepGoal
        )
    }
}
