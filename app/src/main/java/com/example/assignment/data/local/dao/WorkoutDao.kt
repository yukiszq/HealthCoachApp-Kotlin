package com.example.assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.assignment.data.local.entity.WorkoutEntity

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workout_table WHERE userId = :userId ORDER BY CAST(id AS INTEGER) ASC")
    suspend fun getWorkoutsForUser(userId: Int): List<WorkoutEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutEntity): Long

    @Update
    suspend fun update(workout: WorkoutEntity): Int

    @Query("DELETE FROM workout_table WHERE userId = :userId AND id = :workoutId")
    suspend fun deleteById(userId: Int, workoutId: String): Int

    @Query("DELETE FROM workout_table WHERE userId = :userId")
    suspend fun clearForUser(userId: Int): Int
}
