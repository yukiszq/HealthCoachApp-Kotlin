package com.example.assignment.mealscreen

data class NutritionEstimate(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int
)

fun getEstimatedNutrition(foodName: String): NutritionEstimate {

    val food = foodName.lowercase()

    return when {
        "not food" in food ->
            NutritionEstimate(
                calories = 0,
                protein = 0,
                carbs = 0,
                fat = 0,
                fiber = 0
            )

        "fruit" in food ->
            NutritionEstimate(
                calories = 100,
                protein = 1,
                carbs = 25,
                fat = 0,
                fiber = 3
            )

        "vegetable" in food ->
            NutritionEstimate(
                calories = 80,
                protein = 3,
                carbs = 15,
                fat = 1,
                fiber = 5
            )

        "banana" in food ->
            NutritionEstimate(
                calories = 105,
                protein = 1,
                carbs = 27,
                fat = 0,
                fiber = 3
            )

        "apple" in food ->
            NutritionEstimate(
                calories = 95,
                protein = 0,
                carbs = 25,
                fat = 0,
                fiber = 4
            )

        "orange" in food ->
            NutritionEstimate(
                calories = 60,
                protein = 1,
                carbs = 15,
                fat = 0,
                fiber = 3
            )

        "pizza" in food ->
            NutritionEstimate(
                calories = 285,
                protein = 12,
                carbs = 36,
                fat = 10,
                fiber = 3
            )

        "burger" in food ||
                "hamburger" in food ->
            NutritionEstimate(
                calories = 350,
                protein = 18,
                carbs = 30,
                fat = 18,
                fiber = 2
            )

        "fried chicken" in food ->
            NutritionEstimate(
                calories = 320,
                protein = 25,
                carbs = 15,
                fat = 18,
                fiber = 1
            )

        "chicken" in food ->
            NutritionEstimate(
                calories = 250,
                protein = 30,
                carbs = 5,
                fat = 12,
                fiber = 0
            )

        "fried rice" in food ->
            NutritionEstimate(
                calories = 330,
                protein = 10,
                carbs = 45,
                fat = 12,
                fiber = 3
            )

        "rice" in food ->
            NutritionEstimate(
                calories = 200,
                protein = 4,
                carbs = 45,
                fat = 1,
                fiber = 1
            )

        "noodle" in food ||
                "noodles" in food ||
                "pasta" in food ->
            NutritionEstimate(
                calories = 320,
                protein = 10,
                carbs = 50,
                fat = 9,
                fiber = 3
            )

        "sandwich" in food ->
            NutritionEstimate(
                calories = 300,
                protein = 15,
                carbs = 35,
                fat = 12,
                fiber = 4
            )

        "bread" in food ->
            NutritionEstimate(
                calories = 150,
                protein = 5,
                carbs = 28,
                fat = 2,
                fiber = 2
            )

        "salad" in food ->
            NutritionEstimate(
                calories = 150,
                protein = 5,
                carbs = 15,
                fat = 8,
                fiber = 6
            )

        "cake" in food ->
            NutritionEstimate(
                calories = 350,
                protein = 5,
                carbs = 50,
                fat = 15,
                fiber = 1
            )

        "ice cream" in food ->
            NutritionEstimate(
                calories = 200,
                protein = 4,
                carbs = 25,
                fat = 10,
                fiber = 1
            )

        "egg" in food ->
            NutritionEstimate(
                calories = 80,
                protein = 6,
                carbs = 1,
                fat = 5,
                fiber = 0
            )

        "fish" in food ->
            NutritionEstimate(
                calories = 220,
                protein = 25,
                carbs = 3,
                fat = 12,
                fiber = 0
            )

        "soup" in food ->
            NutritionEstimate(
                calories = 180,
                protein = 8,
                carbs = 20,
                fat = 7,
                fiber = 3
            )

        "food" in food ->
            NutritionEstimate(
                calories = 250,
                protein = 10,
                carbs = 30,
                fat = 10,
                fiber = 3
            )

        else ->
            NutritionEstimate(
                calories = 0,
                protein = 0,
                carbs = 0,
                fat = 0,
                fiber = 0
            )
    }
}