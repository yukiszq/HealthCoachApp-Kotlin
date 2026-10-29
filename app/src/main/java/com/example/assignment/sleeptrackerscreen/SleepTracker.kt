package com.example.assignment.sleeptrackerscreen

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
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Sleep
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.components.AppBottomNavigation
import com.example.assignment.model.SleepRecord
import java.text.SimpleDateFormat
import java.util.Locale

private val PrimaryGreen = Color(0xFF0F6826)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val CardBg = Color(0xFFF2F2F7)
private val SleepPurple = Color(0xFF5E35B1)
private val LightPurpleBg = Color(0xFFF1E9FF)

@Composable
fun SleepTrackerScreen(
    selectedDate: String,
    sleepRecords: List<SleepRecord>,
    goalMinutes: Int = 480,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onAddClick: () -> Unit,
    onSleepClick: (SleepRecord) -> Unit,
    onWaterClick: () -> Unit = {},
    onMealClick: () -> Unit = {},
    onExerciseClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInsightClick: () -> Unit = {}
) {
    val totalMinutes = sleepRecords.sumOf { it.durationMinutes }

    val progress = if (goalMinutes > 0) {
        (totalMinutes.toFloat() / goalMinutes.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Scaffold(
        containerColor = ScreenBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Sleep Record")
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
                Spacer(Modifier.height(8.dp))

                Text(
                    "Trackers",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )

                Text(
                    "Keep your healthy habits on track",
                    color = TextMuted
                )
            }

            item {
                DateSelector(
                    selectedDate,
                    onPreviousDate,
                    onNextDate
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    TrackerTab("Water", false, Modifier.weight(1f), onWaterClick)
                    TrackerTab("Meal", false, Modifier.weight(1f), onMealClick)
                    TrackerTab("Exercise", false, Modifier.weight(1f), onExerciseClick)
                    TrackerTab("Sleep", true, Modifier.weight(1f)) {}
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Daily Sleep", color = TextMuted)
                            Text(
                                "${(progress * 100).toInt()}% Goal",
                                color = SleepPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "${formatMinutes(totalMinutes)} / ${formatMinutes(goalMinutes)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleepPurple
                        )

                        Spacer(Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = SleepPurple,
                            trackColor = Color(0xFFE5E5E5)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "${sleepRecords.size} sleep records",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            item {
                Text(
                    "Sleep History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            if (sleepRecords.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No sleep records for this date.",
                            color = TextMuted
                        )
                    }
                }
            } else {
                items(sleepRecords, key = { it.id }) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSleepClick(record) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = LightPurpleBg
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.NightsStay,
                                        contentDescription = null,
                                        tint = SleepPurple
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    record.sleepType,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    "${record.bedtime} - ${record.wakeUpTime}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Text(
                                    formatMinutes(record.durationMinutes),
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
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
        IconButton(onClick = onPreviousDate) {
            Icon(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous date"
            )
        }

        Text(
            formatDate(selectedDate),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        IconButton(onClick = onNextDate) {
            Icon(
                Icons.Default.KeyboardArrowRight,
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
            .clickable { onClick() },
        shape = CircleShape,
        color = if (selected) SleepPurple else CardBg
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else TextDark
            )
        }
    }
}

private fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    val remaining = minutes % 60

    return when {
        hours > 0 && remaining > 0 -> "${hours}h ${remaining}m"
        hours > 0 -> "${hours}h"
        else -> "${remaining}m"
    }
}

private fun formatDate(date: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val output = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)
        output.format(input.parse(date)!!)
    } catch (_: Exception) {
        date
    }
}
