package com.example.assignment.mealscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment.model.MealEntry

@Composable
fun MealHistoryRow(meal: MealEntry, onClick: () -> Unit) {
    val typeColor = when (meal.mealType) {
        "Breakfast" -> Color(0xFFE65100)
        "Lunch" -> Color(0xFF1565C0)
        else -> Color(0xFF2E7D32)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Restaurant, contentDescription = null, tint = typeColor, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(meal.mealType, style = MaterialTheme.typography.labelMedium, color = typeColor)
                Text(meal.foodName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${meal.kcal} kcal • ${meal.time}", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Protein ${meal.protein}g • Carbs ${meal.carbs}g • Fat ${meal.fat}g • Fiber ${meal.fiber}g",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}