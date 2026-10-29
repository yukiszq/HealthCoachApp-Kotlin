package com.example.assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assignment.data.local.entity.GoalsEntity

@Dao
interface GoalsDao {

    @Query("SELECT * FROM goals_table WHERE userId = :userId LIMIT 1")
    suspend fun getGoalsForUser(userId: Int): GoalsEntity?

    /** Insert-or-replace: there is only ever one goals row per user. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goals: GoalsEntity): Long
}
