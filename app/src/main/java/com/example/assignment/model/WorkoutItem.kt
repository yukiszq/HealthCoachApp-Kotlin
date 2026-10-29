package com.example.assignment.model

data class WorkoutItem(
    val id: String,
    val title: String,
    val time: String,
    val duration: String,
    val calories: Int,
    val distanceKm: Double? = null,
    val date: String = "",
    val notes: String = ""
)