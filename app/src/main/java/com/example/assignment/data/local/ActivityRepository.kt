package com.example.assignment.data

import androidx.compose.runtime.mutableStateListOf

data class ActivityCategory(
    val title: String,
    val subtitle: String,
    val metValue: Double
)

object ActivityRepository {

    const val USER_WEIGHT_KG = 70.0

    val categories = mutableStateListOf(
        ActivityCategory("Running", "8.0 METs", 8.0),
        ActivityCategory("Cycling", "6.8 METs", 6.8),
        ActivityCategory("Gym", "5.0 METs", 5.0),
        ActivityCategory("Swimming", "7.0 METs", 7.0),
        ActivityCategory("Badminton", "5.5 METs", 5.5),
        ActivityCategory("Yoga", "3.0 METs", 3.0),
        ActivityCategory("Basketball", "6.5 METs", 6.5),
        ActivityCategory("Other", "4.0 METs", 4.0)
    )

    fun autoDetectMetValue(name: String): Double {
        val query = name.lowercase()

        return when {
            query.contains("run") || query.contains("jog") -> 9.8
            query.contains("sprint") -> 11.5
            query.contains("walk") || query.contains("hike") -> 3.8
            query.contains("cycl") || query.contains("bike") -> 7.5
            query.contains("swim") -> 8.0
            query.contains("gym") || query.contains("weight") || query.contains("lift") -> 6.0
            query.contains("hiit") || query.contains("crossfit") -> 8.0
            query.contains("badminton") || query.contains("tennis") || query.contains("squash") -> 7.0
            query.contains("basket") || query.contains("soccer") || query.contains("football") -> 8.0
            query.contains("yoga") || query.contains("pilates") || query.contains("stretch") -> 3.0
            query.contains("dance") || query.contains("zumba") -> 5.0
            query.contains("jump") || query.contains("rope") -> 11.0
            query.contains("pickleball") || query.contains("pickle ball") -> 6.0
            query.contains("volley") -> 5.5
            else -> 4.0
        }
    }

    fun addCustomActivity(
        name: String,
        customMet: Double? = null
    ) {
        val metValue = customMet ?: autoDetectMetValue(name)

        val newCategory =
            ActivityCategory(
                title = name,
                subtitle = "$metValue METs",
                metValue = metValue
            )

        if (
            categories.none {
                it.title.equals(name, ignoreCase = true)
            }
        ) {
            val otherIndex =
                categories.indexOfFirst {
                    it.title == "Other"
                }

            if (otherIndex >= 0) {
                categories.add(otherIndex, newCategory)
            } else {
                categories.add(newCategory)
            }
        }
    }

    fun getMetForActivity(name: String): Double {
        return categories.find {
            it.title.equals(name, ignoreCase = true)
        }?.metValue ?: autoDetectMetValue(name)
    }

    fun calculateCaloriesBurned(
        met: Double,
        durationSeconds: Long,
        weightKg: Double = USER_WEIGHT_KG
    ): Int {
        val minutes = durationSeconds / 60.0
        val caloriesPerMinute = (met * 3.5 * weightKg) / 200.0
        return (caloriesPerMinute * minutes).toInt()
    }
}
