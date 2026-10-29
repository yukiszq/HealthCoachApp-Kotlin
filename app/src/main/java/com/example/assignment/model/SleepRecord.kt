package com.example.assignment.model

data class SleepRecord(
    val id: Int,
    val date: String,
    val sleepType: String,
    val bedtime: String,
    val wakeUpTime: String,
    val durationMinutes: Int,
    val quality: Int,
    val notes: String = ""
)