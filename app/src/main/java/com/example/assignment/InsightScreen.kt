package com.example.assignment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.components.AppBottomNavigation
import com.example.assignment.data.UserHealthData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

private val InsightGreen = Color(0xFF0F6826)
private val InsightBg = Color(0xFFF9F9FB)
private val InsightText = Color(0xFF1C1C1E)
private val InsightMuted = Color(0xFF8E8E93)
private val InsightCard = Color(0xFFF2F2F7)

private val WaterBlue = Color(0xFF1976D2)
private val SleepPurple = Color(0xFF5E35B1)
private val ExerciseGreen = Color(0xFF0F6826)
private val MealOrange = Color(0xFFD84315)

private enum class HealthInsightType {
    WATER,
    SLEEP,
    EXERCISE,
    MEAL
}

private data class InsightDayValue(
    val label: String,
    val rawValue: Double,
    val progress: Float
)

@Composable
fun InsightScreen(
    userId: Int,
    onHomeClick: () -> Unit,
    onTrackerClick: () -> Unit
) {
    var selectedType by remember {
        mutableStateOf(HealthInsightType.WATER)
    }

    var weekOffset by remember {
        mutableIntStateOf(0)
    }

    val selectedColor = when (selectedType) {
        HealthInsightType.WATER -> WaterBlue
        HealthInsightType.SLEEP -> SleepPurple
        HealthInsightType.EXERCISE -> ExerciseGreen
        HealthInsightType.MEAL -> MealOrange
    }

    val goals = UserHealthData.goals(userId)
    val dates = getWeekDates(weekOffset)
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    val dayValues = dates.mapIndexed { index, date ->

        // keep decimal minutes so short workouts still count
        val raw = when (selectedType) {
            HealthInsightType.WATER ->
                UserHealthData.waterTotal(userId, date).toDouble()

            HealthInsightType.SLEEP ->
                UserHealthData.sleepMinutes(userId, date).toDouble()

            HealthInsightType.EXERCISE ->
                UserHealthData.exerciseMinutes(userId, date)

            HealthInsightType.MEAL ->
                UserHealthData.mealTotal(userId, date).toDouble()
        }

        val goal = when (selectedType) {
            HealthInsightType.WATER ->
                goals.waterGoal.toDouble()

            HealthInsightType.SLEEP ->
                goals.sleepGoal.toDouble()

            HealthInsightType.EXERCISE ->
                goals.exerciseGoal.toDouble()

            HealthInsightType.MEAL ->
                goals.mealGoal.toDouble()
        }

        InsightDayValue(
            label = dayLabels[index],
            rawValue = raw,
            progress = if (goal > 0) {
                (raw / goal)
                    .toFloat()
                    .coerceIn(0f, 1f)
            } else {
                0f
            }
        )
    }

    val activeDays = dayValues.count {
        it.rawValue > 0
    }

    val today = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    ).format(Calendar.getInstance().time)

    // dont include future days in current week progress
    val daysForGoal = dayValues.filterIndexed { index, _ ->
        dates[index] <= today
    }

    val goalRate = if (daysForGoal.isNotEmpty()) {
        (
                daysForGoal
                    .map { it.progress }
                    .average() * 100.0
                ).roundToInt()
    } else {
        0
    }

    val streak = calculateStreak(dayValues)

    val hasData = dayValues.any {
        it.rawValue > 0
    }

    val weeklyMessage = when (selectedType) {

        HealthInsightType.WATER -> {
            when {
                !hasData ->
                    "No water intake recorded this week. Remember to log your water and stay hydrated."

                goalRate < 50 ->
                    "Your water intake is quite low this week. Try to drink more water throughout the day."

                goalRate < 100 ->
                    "You are getting closer to your water goal. Try to drink a little more water each day."

                else ->
                    "Great job! You are meeting your water intake goal. Keep staying hydrated."
            }
        }

        HealthInsightType.SLEEP -> {
            when {
                !hasData ->
                    "No sleep records found this week. Start tracking your sleep to understand your sleep pattern."

                goalRate < 50 ->
                    "You are not getting enough sleep this week. Try to rest more and keep a regular bedtime."

                goalRate < 100 ->
                    "Your sleep is improving, but you are still below your sleep goal. Try to get a little more rest."

                else ->
                    "Great job! You are reaching your sleep goal. Keep maintaining a healthy sleep routine."
            }
        }

        HealthInsightType.EXERCISE -> {
            when {
                !hasData ->
                    "No exercise recorded this week. Try to include some physical activity in your daily routine."

                goalRate < 50 ->
                    "Your exercise time is quite low this week. Try adding more short workouts or walks."

                goalRate < 100 ->
                    "You are making good progress with exercise. A little more activity can help you reach your goal."

                else ->
                    "Great work! You are meeting your exercise goal. Keep staying active."
            }
        }

        HealthInsightType.MEAL -> {
            when {
                !hasData ->
                    "No meals recorded this week. Start logging your meals to keep track of your calorie intake."

                goalRate < 50 ->
                    "Your recorded calorie intake is quite low compared with your goal. Make sure you are logging your meals consistently."

                goalRate < 100 ->
                    "Your calorie intake is getting closer to your daily goal. Keep tracking your meals regularly."

                else ->
                    "You have reached your calorie goal. Keep monitoring your meals and maintain a balanced intake."
            }
        }
    }

    Scaffold(
        containerColor = InsightBg,
        bottomBar = {
            AppBottomNavigation(
                selectedItem = "Insight",
                onHomeClick = onHomeClick,
                onTrackersClick = onTrackerClick,
                onInsightClick = {}
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Health Insight",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = InsightGreen
            )

            Text(
                text = "See your progress and health trends",
                fontSize = 14.sp,
                color = InsightMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (weekOffset > -8) {
                            weekOffset--
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous week"
                    )
                }

                Text(
                    text = "${shortDate(dates.first())} - ${shortDate(dates.last())}",
                    fontWeight = FontWeight.Bold,
                    color = InsightText
                )

                IconButton(
                    onClick = {
                        if (weekOffset < 0) {
                            weekOffset++
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next week"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InsightTab(
                    text = "Water",
                    selected = selectedType == HealthInsightType.WATER,
                    selectedColor = WaterBlue,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedType = HealthInsightType.WATER
                }

                InsightTab(
                    text = "Meal",
                    selected = selectedType == HealthInsightType.MEAL,
                    selectedColor = MealOrange,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedType = HealthInsightType.MEAL
                }

                InsightTab(
                    text = "Exercise",
                    selected = selectedType == HealthInsightType.EXERCISE,
                    selectedColor = ExerciseGreen,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedType = HealthInsightType.EXERCISE
                }

                InsightTab(
                    text = "Sleep",
                    selected = selectedType == HealthInsightType.SLEEP,
                    selectedColor = SleepPurple,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedType = HealthInsightType.SLEEP
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Weekly Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InsightText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OverviewMetric(
                            title = "GOAL",
                            value = "$goalRate%",
                            color = selectedColor
                        )

                        OverviewMetric(
                            title = "ACTIVE DAYS",
                            value = "$activeDays/7",
                            color = selectedColor
                        )

                        OverviewMetric(
                            title = "STREAK",
                            value = "$streak days",
                            color = selectedColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Weekly Trend",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InsightText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dayValues.forEach { day ->

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(90.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(
                                                (8 + day.progress * 82).dp
                                            ),
                                        shape = RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp
                                        ),
                                        color = if (day.rawValue > 0) {
                                            selectedColor
                                        } else {
                                            Color(0xFFE5E5E5)
                                        }
                                    ) {}
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = day.label,
                                    fontSize = 11.sp,
                                    color = InsightMuted
                                )
                            }
                        }
                    }

                    if (!hasData) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "No data available for this week.",
                            color = InsightMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Weekly Insight",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = InsightText
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = weeklyMessage,
                        color = Color(0xFF35633E),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Performance Metrics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InsightText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    performanceMetrics(
                        userId = userId,
                        dates = dates,
                        type = selectedType
                    ).forEach { metric ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = metric.first,
                                color = InsightMuted
                            )

                            Text(
                                text = metric.second,
                                fontWeight = FontWeight.SemiBold,
                                color = InsightText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun InsightTab(
    text: String,
    selected: Boolean,
    selectedColor: Color,
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
        color = if (selected) {
            selectedColor
        } else {
            InsightCard
        }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) {
                    Color.White
                } else {
                    InsightText
                }
            )
        }
    }
}

@Composable
private fun OverviewMetric(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            color = InsightMuted
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun performanceMetrics(
    userId: Int,
    dates: List<String>,
    type: HealthInsightType
): List<Pair<String, String>> {

    return when (type) {

        HealthInsightType.WATER -> {

            val values = dates.map {
                UserHealthData.waterTotal(
                    userId,
                    it
                )
            }

            val total = values.sum()

            // average only days with water records
            val loggedValues = values.filter {
                it > 0
            }

            val average = if (loggedValues.isNotEmpty()) {
                loggedValues.sum() / loggedValues.size
            } else {
                0
            }

            listOf(
                "Average Intake" to "$average ml",
                "Weekly Total" to "$total ml",
                "Days Logged" to "${loggedValues.size}/7"
            )
        }

        HealthInsightType.MEAL -> {

            val values = dates.map {
                UserHealthData.mealTotal(
                    userId,
                    it
                )
            }

            val loggedValues = values.filter {
                it > 0
            }

            // same idea as water, only average days logged
            val average = if (loggedValues.isNotEmpty()) {
                loggedValues.sum() / loggedValues.size
            } else {
                0
            }

            val mealCount = dates.sumOf {
                UserHealthData.mealsForDate(
                    userId,
                    it
                ).size
            }

            listOf(
                "Average Calories" to "$average kcal",
                "Meals Logged" to "$mealCount",
                "Days Logged" to "${loggedValues.size}/7"
            )
        }

        HealthInsightType.EXERCISE -> {

            val values = dates.map {
                UserHealthData.exerciseMinutes(
                    userId,
                    it
                )
            }

            val totalMinutes = values.sum()
            val totalSeconds =
                (totalMinutes * 60).roundToInt()

            val displayMins =
                totalSeconds / 60

            val displaySecs =
                totalSeconds % 60

            val timeText = when {
                displayMins == 0 ->
                    "$displaySecs sec"

                displaySecs == 0 ->
                    "$displayMins min"

                else ->
                    "$displayMins min $displaySecs sec"
            }

            val workoutCount = dates.sumOf {
                UserHealthData.workoutsForDate(
                    userId,
                    it
                ).size
            }

            val calories = dates.sumOf {
                UserHealthData.exerciseCalories(
                    userId,
                    it
                )
            }

            listOf(
                "Exercise Time" to timeText,
                "Workouts" to "$workoutCount",
                "Calories Burned" to "$calories kcal"
            )
        }

        HealthInsightType.SLEEP -> {

            val values = dates.map {
                UserHealthData.sleepMinutes(
                    userId,
                    it
                )
            }

            val active = values.filter {
                it > 0
            }

            val average = if (active.isNotEmpty()) {
                active.sum() / active.size
            } else {
                0
            }

            val records = dates.flatMap {
                UserHealthData.sleepForDate(
                    userId,
                    it
                )
            }

            val quality = if (records.isNotEmpty()) {
                records.map {
                    it.quality
                }.average()
            } else {
                0.0
            }

            listOf(
                "Average Sleep" to formatMinutes(average),
                "Sleep Records" to "${records.size}",
                "Average Quality" to String.format(
                    Locale.US,
                    "%.1f / 5",
                    quality
                )
            )
        }
    }
}

private fun getWeekDates(
    weekOffset: Int
): List<String> {

    val calendar = Calendar.getInstance()
    val currentDay =
        calendar.get(Calendar.DAY_OF_WEEK)

    val daysFromMonday =
        (currentDay - Calendar.MONDAY + 7) % 7

    calendar.add(
        Calendar.DAY_OF_MONTH,
        -daysFromMonday + weekOffset * 7
    )

    val format = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    )

    return List(7) {
        val result =
            format.format(calendar.time)

        calendar.add(
            Calendar.DAY_OF_MONTH,
            1
        )

        result
    }
}

private fun calculateStreak(
    values: List<InsightDayValue>
): Int {

    var streak = 0

    for (value in values.asReversed()) {
        if (value.rawValue > 0) {
            streak++
        } else if (streak > 0) {
            break
        }
    }

    return streak
}

private fun shortDate(
    date: String
): String {

    return try {
        val input = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val output = SimpleDateFormat(
            "d MMM",
            Locale.US
        )

        output.format(
            input.parse(date)!!
        )

    } catch (_: Exception) {
        date
    }
}

private fun formatMinutes(
    minutes: Int
): String {

    val hours = minutes / 60
    val remaining = minutes % 60

    return when {
        hours > 0 && remaining > 0 ->
            "${hours}h ${remaining}m"

        hours > 0 ->
            "${hours}h"

        else ->
            "${remaining}m"
    }
}
@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun InsightScreenPreview() {
    MaterialTheme {
        InsightScreen(
            userId = 1,
            onHomeClick = {},
            onTrackerClick = {}
        )
    }
}