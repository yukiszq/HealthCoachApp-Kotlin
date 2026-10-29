package com.example.assignment.data.local.entity

import androidx.room.Entity
import com.example.assignment.model.MealEntry

/**
 * Local (Room) copy of one [MealEntry].
 *
 * The columns match the [MealEntry] model one-to-one, plus [userId] so that
 * every logged-in user keeps their own rows. The primary key is the pair
 * (userId, id): meal ids are numbered per user (1, 2, 3 ...), so the same id
 * can appear for two different users.
 */
@Entity(tableName = "meal_table", primaryKeys = ["userId", "id"])
data class MealEntity(
    val userId: Int,
    val id: Int,
    val mealType: String,
    val foodName: String,
    val kcal: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int,
    val time: String,
    val date: String,
    val notes: String,
    val imageUri: String
) {
    fun toModel(): MealEntry = MealEntry(
        id = id,
        mealType = mealType,
        foodName = foodName,
        kcal = kcal,
        protein = protein,
        carbs = carbs,
        fat = fat,
        fiber = fiber,
        time = time,
        date = date,
        notes = notes,
        imageUri = imageUri
    )

    companion object {
        fun fromModel(userId: Int, meal: MealEntry) = MealEntity(
            userId = userId,
            id = meal.id,
            mealType = meal.mealType,
            foodName = meal.foodName,
            kcal = meal.kcal,
            protein = meal.protein,
            carbs = meal.carbs,
            fat = meal.fat,
            fiber = meal.fiber,
            time = meal.time,
            date = meal.date,
            notes = meal.notes,
            imageUri = meal.imageUri
        )
    }
}
