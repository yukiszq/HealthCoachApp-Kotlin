package com.example.assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.assignment.data.local.entity.SleepEntity

@Dao
interface SleepDao {

    @Query("SELECT * FROM sleep_table WHERE userId = :userId ORDER BY id ASC")
    suspend fun getSleepForUser(userId: Int): List<SleepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleep: SleepEntity): Long

    @Update
    suspend fun update(sleep: SleepEntity): Int

    @Query("DELETE FROM sleep_table WHERE userId = :userId AND id = :sleepId")
    suspend fun deleteById(userId: Int, sleepId: Int): Int

    @Query("DELETE FROM sleep_table WHERE userId = :userId")
    suspend fun clearForUser(userId: Int): Int
}
