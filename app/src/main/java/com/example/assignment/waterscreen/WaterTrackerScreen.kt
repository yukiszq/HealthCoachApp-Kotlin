package com.example.assignment.waterscreen

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
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.components.AppBottomNavigation
import com.example.assignment.model.WaterRecord
import java.text.SimpleDateFormat
import java.util.Locale

private val PrimaryGreen = Color(0xFF0F6826)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val CardBg = Color(0xFFF2F2F7)
private val WaterBlue = Color(0xFF1976D2)
private val LightBlueBg = Color(0xFFEAF3FF)

@Composable
fun WaterTrackerScreen(
    selectedDate: String,
    records: List<WaterRecord>,
    goalAmount: Int,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onAddClick: () -> Unit,
    onRecordClick: (WaterRecord) -> Unit,
    onMealClick: () -> Unit = {},
    onExerciseClick: () -> Unit = {},
    onSleepClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInsightClick: () -> Unit = {}
) {
    val total = records.sumOf { it.intakeAmount }

    val progress = if (goalAmount > 0) {
        (total.toFloat() / goalAmount.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val remaining = (goalAmount - total).coerceAtLeast(0)

    Scaffold(
        containerColor = ScreenBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Water")
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
                    TrackerTab("Water", true, Modifier.weight(1f)) {}
                    TrackerTab("Meal", false, Modifier.weight(1f), onMealClick)
                    TrackerTab("Exercise", false, Modifier.weight(1f), onExerciseClick)
                    TrackerTab("Sleep", false, Modifier.weight(1f), onSleepClick)
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
                            Text("Daily Water", color = TextMuted)
                            Text(
                                "${(progress * 100).toInt()}% Goal",
                                color = WaterBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "$total / $goalAmount ml",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue
                        )

                        Spacer(Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = WaterBlue,
                            trackColor = Color(0xFFE5E5E5)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            if (remaining > 0) "$remaining ml left"
                            else "Goal reached",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            item {
                Text(
                    "Water History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            if (records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No water records for this date.",
                            color = TextMuted
                        )
                    }
                }
            } else {
                items(records, key = { it.waterId }) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecordClick(record) },
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
                                color = LightBlueBg
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = WaterBlue
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    "${record.intakeAmount} ${record.unit}",
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    record.loggedAt.substringAfter(" ", record.loggedAt),
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
        color = if (selected) WaterBlue else CardBg
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

private fun formatDate(date: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val output = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)
        output.format(input.parse(date)!!)
    } catch (_: Exception) {
        date
    }
}
