package com.example.assignment.exercisescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.assignment.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistoryScreen(
    workouts: List<WorkoutItem>,
    onBack: () -> Unit,
    onWorkoutClick: (WorkoutItem) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTrackersClick: () -> Unit = {},
    onInsightClick: () -> Unit = {}
) {
    val weeklyWorkouts = remember(workouts) {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = (currentDay - Calendar.MONDAY + 7) % 7
        
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday)
        val startOfWeek = calendar.time
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        workouts.filter {
            try {
                val date = sdf.parse(it.date)
                date != null && !date.before(startOfWeek)
            } catch (e: Exception) {
                false
            }
        }
    }

    val totalTimeFormatted = remember(weeklyWorkouts) {
        var totalSeconds = 0L

        weeklyWorkouts.forEach { workout ->
            val duration = workout.duration.lowercase()
            val minutesMatch = Regex("(\\d+)\\s*(mins?|m)").find(duration)
            val secondsMatch = Regex("(\\d+)\\s*(secs?|s)").find(duration)

            if (minutesMatch == null && secondsMatch == null) {
                totalSeconds += duration.trim().toLongOrNull() ?: 0L
            } else {
                val mins = minutesMatch?.groupValues?.get(1)?.toLongOrNull() ?: 0L
                val secs = secondsMatch?.groupValues?.get(1)?.toLongOrNull() ?: 0L
                totalSeconds += (mins * 60) + secs
            }
        }

        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> if (seconds > 0) "${minutes}m ${seconds}s" else "${minutes}m"
            else -> "${seconds}s"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Workout History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkText
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = DarkText)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = DarkText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFAFAFA))
            )
        },
        bottomBar = {
            AppBottomNavigation(
                selectedItem = "Trackers",
                onHomeClick = onHomeClick,
                onTrackersClick = onTrackersClick,
                onInsightClick = onInsightClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(LightGreenBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Total Workouts", fontSize = 12.sp, color = GrayText)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${weeklyWorkouts.size}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(
                                    text = " / wk",
                                    fontSize = 12.sp,
                                    color = GrayText,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFE8F1FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFF2F80ED),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Total Time", fontSize = 12.sp, color = GrayText)
                            Text(
                                text = totalTimeFormatted,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
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
                        text = "Recent Activities",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }
            }

            items(workouts) { workout ->
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

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWorkoutClick(workout) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = workout.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )

                            val displayDate = try {
                                val input = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                val output = java.text.SimpleDateFormat("d MMM", Locale.US)
                                output.format(input.parse(workout.date)!!)
                            } catch (e: Exception) {
                                workout.date
                            }

                            val subText = buildString {
                                append("$displayDate • ${formatWorkoutHistoryDuration(workout.duration)}")
                                workout.distanceKm?.let { dist ->
                                    if (dist > 0.0) {
                                        append(" • ${String.format(Locale.US, "%.2f", dist)} km")
                                    }
                                }
                            }

                            Text(
                                text = subText,
                                fontSize = 13.sp,
                                color = GrayText
                            )
                        }

                        Text(
                            text = "${workout.calories} kcal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                }
            }
        }
    }
}

private fun formatWorkoutHistoryDuration(rawDuration: String): String {
    val trimmed = rawDuration.trim().lowercase()

    val minsMatch = Regex("""(\d+)\s*(?:min|mins|m)""").find(trimmed)
    val secsMatch = Regex("""(\d+)\s*(?:sec|secs|s)""").find(trimmed)

    if (minsMatch != null || secsMatch != null) {
        val mins = minsMatch?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L
        val secs = secsMatch?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L

        return when {
            mins == 0L -> "${secs}s"
            secs == 0L -> "${mins}m"
            else -> "${mins}m ${secs}s"
        }
    }

    val plainSeconds = trimmed.toLongOrNull() ?: return rawDuration
    val mins = plainSeconds / 60
    val secs = plainSeconds % 60

    return when {
        mins == 0L -> "${secs}s"
        secs == 0L -> "${mins}m"
        else -> "${mins}m ${secs}s"
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WorkoutHistoryScreenPreview() {
    val sampleWorkouts = listOf(
        WorkoutItem("1", "Running", "08:30 AM", "30 mins", 294, distanceKm = 4.25),
        WorkoutItem("2", "Squash", "01:15 PM", "45 mins", 420, distanceKm = null)
    )
    WorkoutHistoryScreen(
        workouts = sampleWorkouts,
        onBack = {}
    )
}