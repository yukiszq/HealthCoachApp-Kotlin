package com.example.assignment.model

data class WaterRecord(
    val waterId: Int,
    val userId: Int,
    val intakeAmount: Int,
    val unit: String,
    val loggedAt: String
)