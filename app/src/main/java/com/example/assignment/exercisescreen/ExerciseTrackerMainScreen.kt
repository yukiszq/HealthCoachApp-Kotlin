package com.example.assignment.exercisescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.components.AppBottomNavigation
import com.example.assignment.model.WorkoutItem
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.ceil

private val PrimaryGreen = Color(0xFF0F6826)
private val LightGreenBg = Color(0xFFE8F5E9)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val CardBg = Color(0xFFF2F2F7)

@Composable
fun ExerciseTrackerMainScreen(
    selectedDate: String = "2026-10-24",
    workouts: List<WorkoutItem>,
    exerciseGoal: Int = 60,
    onPreviousDate: () -> Unit = {},
    onNextDate: () -> Unit = {},
    onAddClick: () -> Unit,
    onWorkoutClick: (WorkoutItem) -> Unit,
    onViewAllClick: () -> Unit,
    onWaterClick: () -> Unit = {},
    onMealClick: () -> Unit = {},
    onSleepClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInsightClick: () -> Unit = {}
) {
    val totalSeconds = workouts.sumOf { getDurationInSeconds(it.duration) }
    val totalCalories = workouts.sumOf { it.calories }

    val progress = if (exerciseGoal > 0) {
        (totalSeconds.toFloat() / (exerciseGoal * 60f)).coerceIn(0f, 1f)
    } else {
        0f
    }

    val displayDuration = formatDurationAbbreviated(totalSeconds)
    
    val secondsLeft = (exerciseGoal * 60.0 - totalSeconds).toInt().coerceAtLeast(0)
    val timeLeftText = when {
        secondsLeft == 0 -> "Goal reached!"
        secondsLeft < 60 -> "$secondsLeft secs left"
        else -> "${ceil(secondsLeft / 60.0).toInt()} mins left"
    }

    Scaffold(
        containerColor = ScreenBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add workout"
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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
                            selected = false,
                            modifier = Modifier.weight(1f),
                            onClick = onMealClick
                        )

                        TrackerTab(
                            text = "Exercise",
                            selected = true,
                            modifier = Modifier.weight(1f),
                            onClick = {}
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
                                    text = "Daily Activity",
                                    color = TextMuted
                                )

                                Text(
                                    text = "${(progress * 100).toInt()}% Goal",
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "$displayDuration / $exerciseGoal mins",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = PrimaryGreen,
                                trackColor = Color(0xFFE5E5E5)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${workouts.size} workouts",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )

                                Text(
                                    text = timeLeftText,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "$totalCalories kcal burned",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Workout History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        if (workouts.isNotEmpty()) {
                            Text(
                                text = "View All",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen,
                                modifier = Modifier.clickable {
                                    onViewAllClick()
                                }
                            )
                        }
                    }
                }

                if (workouts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No workouts recorded for this date.",
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    items(
                        items = workouts,
                        key = { it.id }
                    ) { workout ->
                        WorkoutRowItem(
                            workout = workout,
                            onClick = {
                                onWorkoutClick(workout)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Small "added successfully" pill shown at the bottom of the app after a
 * tracker record is saved. Driven from AppNavGraph so every tracker shares it.
 */
@Composable
fun SuccessPillSnackbar(
    visible: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(visible, message) {
        if (visible) {
            delay(2500L)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Color(0xFFEBECEE),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(Color(0xFF007A33), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = message,
                    color = Color(0xFF1C1B1F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
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
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
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
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
                PrimaryGreen
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

@Composable
private fun WorkoutRowItem(
    workout: WorkoutItem,
    onClick: () -> Unit
) {
    val (bgColor, iconColor, icon) = when {
        workout.title.contains("Run", true) || workout.title.contains("Jog", true) -> Triple(Color(0xFFE8F1FF), Color(0xFF2F80ED), Icons.AutoMirrored.Filled.DirectionsRun)
        workout.title.contains("Cycl", true) || workout.title.contains("Bike", true) -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), Icons.AutoMirrored.Filled.DirectionsBike)
        workout.title.contains("Walk", true) -> Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), Icons.AutoMirrored.Filled.DirectionsWalk)
        workout.title.contains("Swim", true) -> Triple(Color(0xFFE0F7FA), Color(0xFF00ACC1), Icons.Default.Pool)
        workout.title.contains("Squash", true) || workout.title.contains("Badminton", true) || workout.title.contains("Tennis", true) || workout.title.contains("Pickle", true) -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), Icons.Default.SportsTennis)
        workout.title.contains("Ping", true) || workout.title.contains("Table Tennis", true) -> Triple(Color(0xFFFFEBEE), Color(0xDDE53935), Icons.Default.SportsBaseball)
        workout.title.contains("Yoga", true) || workout.title.contains("Pilates", true) || workout.title.contains("Stretch", true) -> Triple(Color(0xFFF3E5F5), Color(0xFF8E24AA), Icons.Default.SelfImprovement)
        workout.title.contains("Hike", true) -> Triple(Color(0xFFEFEBE9), Color(0xFF5D4037), Icons.Default.Hiking)
        workout.title.contains("Basket", true) -> Triple(Color(0xFFFBE9E7), Color(0xFFD84315), Icons.Default.SportsBasketball)
        workout.title.contains("Box", true) || workout.title.contains("Martial", true) -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.SportsMartialArts)
        workout.title.contains("Foot", true) || workout.title.contains("Soc", true) -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.SportsSoccer)
        workout.title.contains("Volley", true) -> Triple(Color(0xFFFFF3E0), Color(0xFFEF6C00), Icons.Default.SportsVolleyball)
        workout.title.contains("Dance", true) || workout.title.contains("Zumba", true) -> Triple(Color(0xFFFCE4EC), Color(0xFFAD1457), Icons.Default.SportsKabaddi)
        workout.title.contains("Gym", true) || workout.title.contains("Weight", true) || workout.title.contains("Lift", true) -> Triple(Color(0xFFFFF3E0), Color(0xFFE67E22), Icons.Default.FitnessCenter)
        else -> Triple(LightGreenBg, PrimaryGreen, Icons.Default.FitnessCenter)
    }

    val displayDuration = formatWorkoutDurationText(workout.duration)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = workout.time,
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Text(
                    text = workout.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text = "$displayDuration • ${workout.calories} kcal",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted
            )
        }
    }
}

private fun formatWorkoutDurationText(rawDuration: String): String {
    val trimmed = rawDuration.trim().lowercase()

    val minsMatch = Regex("""(\d+)\s*(?:min|mins|m)""").find(trimmed)
    val secsMatch = Regex("""(\d+)\s*(?:sec|secs|s)""").find(trimmed)

    if (minsMatch != null || secsMatch != null) {
        val mins = minsMatch?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L
        val secs = secsMatch?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L

        return when {
            mins == 0L -> "$secs secs"
            secs == 0L -> "$mins mins"
            else -> "$mins m $secs s"
        }
    }

    val plainSeconds = trimmed.toLongOrNull() ?: return rawDuration
    val mins = plainSeconds / 60
    val secs = plainSeconds % 60

    return when {
        mins == 0L -> "$secs secs"
        secs == 0L -> "$mins mins"
        else -> "$mins m $secs s"
    }
}

private fun getDurationInSeconds(duration: String): Double {
    val trimmed = duration.trim().lowercase()
    val minsMatch = Regex("""(\d+)\s*(?:min|mins|m)""").find(trimmed)
    val secsMatch = Regex("""(\d+)\s*(?:sec|secs|s)""").find(trimmed)

    var totalSeconds = 0.0
    if (minsMatch != null) {
        totalSeconds += (minsMatch.groupValues[1].toDoubleOrNull() ?: 0.0) * 60.0
    }
    if (secsMatch != null) {
        totalSeconds += secsMatch.groupValues[1].toDoubleOrNull() ?: 0.0
    }

    if (minsMatch == null && secsMatch == null) {
        totalSeconds = trimmed.toDoubleOrNull() ?: 0.0
    }
    return totalSeconds
}

private fun formatDurationAbbreviated(totalSeconds: Double): String {
    val totalSecondsInt = totalSeconds.toLong()
    val mins = totalSecondsInt / 60
    val secs = totalSecondsInt % 60

    return when {
        mins == 0L -> "${secs}s"
        secs == 0L -> "${mins}m"
        else -> "${mins}m ${secs}s"
    }
}

private fun formatDate(
    date: String
): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val output = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)

        output.format(input.parse(date)!!)
    } catch (_: Exception) {
        date
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExerciseTrackerMainScreenPreview() {
    val sampleWorkouts = listOf(
        WorkoutItem(
            id = "1",
            title = "Running",
            time = "07:30 AM",
            duration = "10 secs",
            calories = 10
        ),
        WorkoutItem(
            id = "2",
            title = "Gym",
            time = "05:15 PM",
            duration = "45 mins",
            calories = 310
        )
    )

    MaterialTheme {
        ExerciseTrackerMainScreen(
            selectedDate = "2026-10-24",
            workouts = sampleWorkouts,
            exerciseGoal = 60,
            onPreviousDate = {},
            onNextDate = {},
            onAddClick = {},
            onWorkoutClick = {},
            onViewAllClick = {}
        )
    }
}