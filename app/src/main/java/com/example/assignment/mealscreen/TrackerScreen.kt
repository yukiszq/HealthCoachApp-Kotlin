package com.example.assignment.mealscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.components.AppBottomNavigation
import com.example.assignment.model.MealEntry
import java.text.SimpleDateFormat
import java.util.Locale

private val PrimaryGreen = Color(0xFF0F6826)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val CardBg = Color(0xFFF2F2F7)
private val MealOrange = Color(0xFFD84315)

@Composable
fun TrackerScreen(
    selectedDate: String,
    meals: List<MealEntry>,
    goalCalories: Int,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onAddClick: () -> Unit,
    onMealClick: (MealEntry) -> Unit,
    onWaterClick: () -> Unit = {},
    onExerciseClick: () -> Unit = {},
    onSleepClick: () -> Unit = {},
    onHomeClick: () -> Unit,
    onInsightClick: () -> Unit
) {
    val totalKcal = meals.sumOf { it.kcal }

    val progress = if (goalCalories > 0) {
        // Calculate the percentage of the daily calorie goal completed
        // and limit the value between 0 and 1 for the progress bar.
        (totalKcal.toFloat() / goalCalories.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    // Calculate the remaining calories and prevent a negative value.
    val kcalLeft = (goalCalories - totalKcal).coerceAtLeast(0)

    Scaffold(
        containerColor = ScreenBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MealOrange,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add meal"
                )
            }
        },
        bottomBar = {
            AppBottomNavigation(
                selectedItem = "Trackers",
                onHomeClick = onHomeClick,
                onTrackersClick = {},
                onInsightClick = onInsightClick
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Trackers",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )

                Text(
                    text = "Keep your healthy habits on track",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }

            item {
                DateSelector(
                    selectedDate = selectedDate,
                    onPreviousDate = onPreviousDate,
                    onNextDate = onNextDate
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    TrackerTab(
                        text = "Water",
                        selected = false,
                        modifier = Modifier.weight(1f),
                        onClick = onWaterClick
                    )

                    TrackerTab(
                        text = "Meal",
                        selected = true,
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )

                    TrackerTab(
                        text = "Exercise",
                        selected = false,
                        modifier = Modifier.weight(1f),
                        onClick = onExerciseClick
                    )

                    TrackerTab(
                        text = "Sleep",
                        selected = false,
                        modifier = Modifier.weight(1f),
                        onClick = onSleepClick
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Daily Calories",
                                color = TextMuted
                            )

                            Text(
                                text = "${(progress * 100).toInt()}% Goal",
                                fontWeight = FontWeight.Bold,
                                color = MealOrange
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$totalKcal / $goalCalories kcal",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MealOrange
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MealOrange,
                            trackColor = Color(0xFFE5E5E5)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${meals.size} meals logged",
                                fontSize = 12.sp,
                                color = TextMuted
                            )

                            Text(
                                text = "$kcalLeft kcal left",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Meal History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            if (meals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No meals logged for this date.",
                            color = TextMuted
                        )
                    }
                }
            } else {
                items(
                    items = meals,
                    key = { it.id }
                ) { meal ->

                    MealHistoryRow(
                        meal = meal,
                        onClick = {
                            onMealClick(meal)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DateSelector(
    selectedDate: String,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onPreviousDate
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous date"
            )
        }

        Text(
            text = formatDate(selectedDate),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        IconButton(
            onClick = onNextDate
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next date"
            )
        }
    }
}

@Composable
private fun TrackerTab(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(38.dp)
            .clickable {
                onClick()
            },
        shape = CircleShape,
        color =
            if (selected) {
                MealOrange
            } else {
                CardBg
            }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (selected) {
                        Color.White
                    } else {
                        TextDark
                    }
            )
        }
    }
}

private fun formatDate(
    date: String
): String {
    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        val output =
            SimpleDateFormat(
                "EEE, d MMM yyyy",
                Locale.US
            )

        output.format(
            input.parse(date)!!
        )

    } catch (_: Exception) {
        date
    }
}