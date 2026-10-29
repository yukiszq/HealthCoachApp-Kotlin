package com.example.assignment.model

data class MealEntry(
    val id: Int,
    val mealType: String,
    val foodName: String,
    val kcal: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int = 0,
    val time: String,
    val date: String = "",
    val notes: String = "",
    val imageUri: String = ""
)