package com.example.assignment.mealscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment.model.MealEntry
import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

private val ScreenBg = Color(0xFFF9F9FB)
private val PrimaryGreen = Color(0xFF0F6826)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    meal: MealEntry,
    dailyTarget: Int,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val progress = (meal.kcal.toFloat() / dailyTarget).coerceIn(0f, 1f)

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meal Details",
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBg
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryGreen
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // =========================
            // MEAL INFORMATION
            // =========================

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        meal.mealType,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {

                        // Dynamic date
                        Text(
                            meal.date.ifBlank { "Today" },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        Text(
                            "Logged at ${meal.time}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

            // =========================
            // FOOD IMAGE
            // =========================

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                if (meal.imageUri.isNotBlank()) {

                    AsyncImage(
                        model = Uri.parse(meal.imageUri),
                        contentDescription = meal.foodName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            Icons.Filled.Fastfood,
                            contentDescription = meal.foodName,
                            modifier = Modifier.size(60.dp),
                            tint = PrimaryGreen
                        )
                    }
                }
            }

            // =========================
            // CALORIES
            // =========================

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Total Energy",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Text(
                        "${meal.kcal} kcal",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "${(progress * 100).toInt()}% of daily goal",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // =========================
            // NUTRIENTS
            // =========================

            NutrientDetailRow(
                icon = Icons.Filled.FitnessCenter,
                label = "Protein",
                value = "${meal.protein}g",
                color = Color(0xFF1565C0)
            )

            NutrientDetailRow(
                icon = Icons.Filled.Grain,
                label = "Carbs",
                value = "${meal.carbs}g",
                color = Color(0xFFE65100)
            )

            NutrientDetailRow(
                icon = Icons.Filled.Opacity,
                label = "Fats",
                value = "${meal.fat}g",
                color = Color(0xFFC62828)
            )

            NutrientDetailRow(
                icon = Icons.Filled.Grain,
                label = "Fiber",
                value = "${meal.fiber}g",
                color = Color(0xFF2E7D32)
            )

            // =========================
            // NOTES
            // =========================

            if (meal.notes.isNotBlank()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            "Personal Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "\"${meal.notes}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDark
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // =========================
            // EDIT
            // =========================

            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text("Edit")
            }

            // =========================
            // DELETE
            // =========================

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC62828)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text("Delete")
            }
        }
    }
}


// =====================================
// NUTRIENT ROW
// =====================================

@Composable
fun NutrientDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    icon,
                    contentDescription = null,
                    tint = color
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = label,
                    color = TextDark
                )
            }

            Text(
                value,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}