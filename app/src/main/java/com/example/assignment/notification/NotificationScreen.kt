package com.example.assignment.notification

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val PrimaryGreen = Color(0xFF0F6826)
private val LightGreenBg = Color(0xFFE8F5E9)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val DividerColor = Color(0xFFE5E5EA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    currentWaterReminder: Boolean = false,
    currentWaterTime: String = "10:00",
    currentMealReminder: Boolean = false,
    currentMealTime: String = "12:30",
    currentExerciseReminder: Boolean = false,
    currentExerciseTime: String = "18:00",
    currentSleepReminder: Boolean = false,
    currentSleepTime: String = "22:30",
    currentAchievementNotification: Boolean = false,
    onBack: () -> Unit,
    onSave: (
        waterReminder: Boolean,
        waterTime: String,
        mealReminder: Boolean,
        mealTime: String,
        exerciseReminder: Boolean,
        exerciseTime: String,
        sleepReminder: Boolean,
        sleepTime: String,
        achievementNotification: Boolean
    ) -> Unit,
    onTest: () -> Unit = {}
) {
    val context = LocalContext.current

    var waterReminder by remember(currentWaterReminder) {
        mutableStateOf(currentWaterReminder)
    }
    var waterTime by remember(currentWaterTime) {
        mutableStateOf(currentWaterTime)
    }
    var mealReminder by remember(currentMealReminder) {
        mutableStateOf(currentMealReminder)
    }
    var mealTime by remember(currentMealTime) {
        mutableStateOf(currentMealTime)
    }
    var exerciseReminder by remember(currentExerciseReminder) {
        mutableStateOf(currentExerciseReminder)
    }
    var exerciseTime by remember(currentExerciseTime) {
        mutableStateOf(currentExerciseTime)
    }
    var sleepReminder by remember(currentSleepReminder) {
        mutableStateOf(currentSleepReminder)
    }
    var sleepTime by remember(currentSleepTime) {
        mutableStateOf(currentSleepTime)
    }
    var achievementNotification by remember(currentAchievementNotification) {
        mutableStateOf(currentAchievementNotification)
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Notifications",
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
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(13.dp))

                    Column {
                        Text(
                            "Stay on Track",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            "Choose the reminders that are useful for your routine.",
                            fontSize = 12.sp,
                            color = Color(0xFF4F6F57)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Daily Reminders",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(Modifier.height(10.dp))

            ReminderCard(
                icon = Icons.Default.WaterDrop,
                iconBg = Color(0xFFEAF3FF),
                iconColor = Color(0xFF1976D2),
                title = "Water Reminder",
                subtitle = "Stay hydrated throughout the day",
                enabled = waterReminder,
                time = waterTime,
                onToggle = { waterReminder = it },
                onTimeClick = {
                    showTimePicker(context, waterTime) {
                        waterTime = it
                    }
                }
            )

            Spacer(Modifier.height(11.dp))

            ReminderCard(
                icon = Icons.Default.Restaurant,
                iconBg = Color(0xFFFFF0E6),
                iconColor = Color(0xFFD84315),
                title = "Meal Reminder",
                subtitle = "Remember to log your meals",
                enabled = mealReminder,
                time = mealTime,
                onToggle = { mealReminder = it },
                onTimeClick = {
                    showTimePicker(context, mealTime) {
                        mealTime = it
                    }
                }
            )

            Spacer(Modifier.height(11.dp))

            ReminderCard(
                icon = Icons.Default.FitnessCenter,
                iconBg = LightGreenBg,
                iconColor = PrimaryGreen,
                title = "Exercise Reminder",
                subtitle = "Keep your activity routine moving",
                enabled = exerciseReminder,
                time = exerciseTime,
                onToggle = { exerciseReminder = it },
                onTimeClick = {
                    showTimePicker(context, exerciseTime) {
                        exerciseTime = it
                    }
                }
            )

            Spacer(Modifier.height(11.dp))

            ReminderCard(
                icon = Icons.Default.NightsStay,
                iconBg = Color(0xFFF1E9FF),
                iconColor = Color(0xFF5E35B1),
                title = "Sleep Reminder",
                subtitle = "Wind down at a consistent time",
                enabled = sleepReminder,
                time = sleepTime,
                onToggle = { sleepReminder = it },
                onTimeClick = {
                    showTimePicker(context, sleepTime) {
                        sleepTime = it
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Other Alerts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(Modifier.height(10.dp))

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
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFF4E5)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFE58A00)
                            )
                        }
                    }

                    Spacer(Modifier.width(13.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Achievement Alerts",
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            "Notify me when I unlock a new badge",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Switch(
                        checked = achievementNotification,
                        onCheckedChange = { achievementNotification = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryGreen
                        )
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    onSave(
                        waterReminder,
                        waterTime,
                        mealReminder,
                        mealTime,
                        exerciseReminder,
                        exerciseTime,
                        sleepReminder,
                        sleepTime,
                        achievementNotification
                    )
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save Reminders", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onTest,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Send a test notification now")
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "Use this to check notifications are allowed. A reminder set for a " +
                        "time later today will appear the same way.",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ReminderCard(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    enabled: Boolean,
    time: String,
    onToggle: (Boolean) -> Unit,
    onTimeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = iconBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconColor)
                    }
                }

                Spacer(Modifier.width(13.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(subtitle, fontSize = 11.sp, color = TextMuted)
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryGreen
                    )
                )
            }

            if (enabled) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 75.dp),
                    color = DividerColor
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTimeClick() }
                        .padding(
                            start = 75.dp,
                            end = 16.dp,
                            top = 11.dp,
                            bottom = 13.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Reminder time",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(shape = CircleShape, color = LightGreenBg) {
                        Text(
                            time,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun showTimePicker(
    context: android.content.Context,
    time: String,
    onTimeSelected: (String) -> Unit
) {
    val parts = time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    TimePickerDialog(
        context,
        { _, h, m ->
            onTimeSelected(
                String.format(Locale.US, "%02d:%02d", h, m)
            )
        },
        hour,
        minute,
        true
    ).show()
}
