package com.example.assignment.model

import androidx.compose.ui.graphics.vector.ImageVector

data class AchievementItem(
    val id: Int,
    val title: String,
    val description: String,
    val progress: Int,
    val target: Int,
    val unlocked: Boolean,
    val icon: ImageVector
)