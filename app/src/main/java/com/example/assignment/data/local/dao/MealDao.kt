package com.example.assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.assignment.data.local.entity.MealEntity

/**
 * CRUD for the meal_table. Same shape as PersonDao in RoomDbTest (Practical 7):
 * a read query plus suspend insert / update / delete.
 */
@Dao
interface MealDao {

    @Query("SELECT * FROM meal_table WHERE userId = :userId ORDER BY id ASC")
    suspend fun getMealsForUser(userId: Int): List<MealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MealEntity): Long

    @Update
    suspend fun update(meal: MealEntity): Int

    @Query("DELETE FROM meal_table WHERE userId = :userId AND id = :mealId")
    suspend fun deleteById(userId: Int, mealId: Int): Int

    @Query("DELETE FROM meal_table WHERE userId = :userId")
    suspend fun clearForUser(userId: Int): Int
}
