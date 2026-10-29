package com.example.assignment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    // NOT auto-generated: the id is derived from the email (see UserData.stableId)
    // so the same account gets the same id on every device — needed for Supabase sync.
    @PrimaryKey val id: Int,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val age: Int = 0,
    val gender: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val profileImageUri: String = "",
    val profileSetupCompleted: Boolean = false
)