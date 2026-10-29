package com.example.assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.assignment.data.local.entity.WaterEntity

@Dao
interface WaterDao {

    @Query("SELECT * FROM water_table WHERE userId = :userId ORDER BY loggedAt ASC")
    suspend fun getWaterForUser(userId: Int): List<WaterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(water: WaterEntity): Long

    @Update
    suspend fun update(water: WaterEntity): Int

    @Query("DELETE FROM water_table WHERE userId = :userId AND waterId = :waterId")
    suspend fun deleteById(userId: Int, waterId: Int): Int

    @Query("DELETE FROM water_table WHERE userId = :userId")
    suspend fun clearForUser(userId: Int): Int
}
