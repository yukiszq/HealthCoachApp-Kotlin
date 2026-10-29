package com.example.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryGreen = Color(0xFF0F6826)
private val LightGreenBg = Color(0xFFE8F5E9)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val DividerColor = Color(0xFFE5E5EA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthGoalsScreen(
    currentWaterGoal: Int = 2000,
    currentMealGoal: Int = 2200,
    currentExerciseGoal: Int = 60,
    currentSleepGoal: Int = 480,
    onBack: () -> Unit,
    onSave: (
        waterGoal: Int,
        mealGoal: Int,
        exerciseGoal: Int,
        sleepGoal: Int
    ) -> Unit
) {
    var water by remember(currentWaterGoal) {
        mutableStateOf(currentWaterGoal.toString())
    }
    var meal by remember(currentMealGoal) {
        mutableStateOf(currentMealGoal.toString())
    }
    var exercise by remember(currentExerciseGoal) {
        mutableStateOf(currentExerciseGoal.toString())
    }
    var sleepHours by remember(currentSleepGoal) {
        mutableStateOf(
            if (currentSleepGoal % 60 == 0) {
                (currentSleepGoal / 60).toString()
            } else {
                String.format(
                    java.util.Locale.US,
                    "%.1f",
                    currentSleepGoal / 60.0
                )
            }
        )
    }
    var error by remember { mutableStateOf("") }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Health Goals",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PrimaryGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ScreenBg
                    )
                )
                HorizontalDivider(color = DividerColor)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreenBg)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = RoundedCornerShape(15.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Flag,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(13.dp))
                    Column {
                        Text(
                            "Set Your Daily Targets",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            "Adjust goals that match your daily routine.",
                            fontSize = 12.sp,
                            color = Color(0xFF4F6F57)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            GoalCard(
                icon = Icons.Default.WaterDrop,
                iconBackground = Color(0xFFEAF3FF),
                iconColor = Color(0xFF1976D2),
                title = "Water",
                subtitle = "Daily hydration target",
                value = water,
                unit = "ml",
                onValueChange = { water = it.filter(Char::isDigit) }
            )

            Spacer(Modifier.height(12.dp))

            GoalCard(
                icon = Icons.Default.Restaurant,
                iconBackground = Color(0xFFFFF0E6),
                iconColor = Color(0xFFD84315),
                title = "Meal Calories",
                subtitle = "Daily calorie target",
                value = meal,
                unit = "kcal",
                onValueChange = { meal = it.filter(Char::isDigit) }
            )

            Spacer(Modifier.height(12.dp))

            GoalCard(
                icon = Icons.Default.FitnessCenter,
                iconBackground = LightGreenBg,
                iconColor = PrimaryGreen,
                title = "Exercise",
                subtitle = "Daily activity target",
                value = exercise,
                unit = "mins",
                onValueChange = { exercise = it.filter(Char::isDigit) }
            )

            Spacer(Modifier.height(12.dp))

            GoalCard(
                icon = Icons.Default.NightsStay,
                iconBackground = Color(0xFFF1E9FF),
                iconColor = Color(0xFF5E35B1),
                title = "Sleep",
                subtitle = "Daily sleep target",
                value = sleepHours,
                unit = "hours",
                decimal = true,
                onValueChange = { sleepHours = it }
            )

            if (error.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFEBEE)
                ) {
                    Text(
                        error,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    val waterValue = water.toIntOrNull()
                    val mealValue = meal.toIntOrNull()
                    val exerciseValue = exercise.toIntOrNull()
                    val sleepValue = sleepHours.toDoubleOrNull()

                    if (
                        waterValue == null ||
                        mealValue == null ||
                        exerciseValue == null ||
                        sleepValue == null ||
                        waterValue <= 0 ||
                        mealValue <= 0 ||
                        exerciseValue <= 0 ||
                        sleepValue <= 0
                    ) {
                        error = "Please enter valid goal values."
                    } else {
                        error = ""
                        onSave(
                            waterValue,
                            mealValue,
                            exerciseValue,
                            (sleepValue * 60).toInt()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save Goals", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun GoalCard(
    icon: ImageVector,
    iconBackground: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    value: String,
    unit: String,
    decimal: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(15.dp),
                color = iconBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(13.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = TextDark)
                Text(subtitle, fontSize = 11.sp, color = TextMuted)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    suffix = { Text(unit, color = TextMuted) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (decimal) {
                            KeyboardType.Decimal
                        } else {
                            KeyboardType.Number
                        }
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }
    }
}
