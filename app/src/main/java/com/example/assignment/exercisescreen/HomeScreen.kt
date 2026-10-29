package com.example.assignment.exercisescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.components.AppBottomNavigation
import com.example.assignment.components.AppNavigationDrawer
import com.example.assignment.data.UserHealthData.exerciseMinutes
import com.example.assignment.data.UserHealthData.sleepMinutes
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val PrimaryGreen =
    Color(0xFF0F6826)

private val LightGreenBg =
    Color(0xFFE8F5E9)

private val ScreenBg =
    Color(0xFFF9F9FB)

private val TextDark =
    Color(0xFF1C1C1E)

private val TextMuted =
    Color(0xFF8E8E93)

private val LightBlueBg =
    Color(0xFFEAF3FF)

private val WaterBlue =
    Color(0xFF1976D2)

private val LightOrangeBg =
    Color(0xFFFFF0E6)

private val MealOrange =
    Color(0xFFD84315)

private val LightPurpleBg =
    Color(0xFFF1E9FF)

private val SleepPurple =
    Color(0xFF5E35B1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String = "User",
    profileImageUri: String = "",
    waterIntake: Int = 0,
    waterGoal: Int = 2000,
    mealCalories: Int = 0,
    mealGoal: Int = 2200,
    exerciseMinutes: Double = 0.0,
    exerciseGoal: Int = 60,
    sleepMinutes: Int = 0,
    sleepGoal: Int = 480,
    onNavigateToEditGoals: () -> Unit,
    onNavigateToExerciseTracker: () -> Unit,
    onNavigateToMealTracker: () -> Unit,
    onNavigateToWaterTracker: () -> Unit = {},
    onNavigateToSleepTracker: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAchievementClick: () -> Unit = {},
    onHealthGoalsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onBmiClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onInsightClick: () -> Unit = {},
    onTrackerClick:()->Unit={}
) {

    val drawerState =
        rememberDrawerState(
            DrawerValue.Closed
        )

    val scope =
        rememberCoroutineScope()

    val waterProgress =
        progress(
            waterIntake,
            waterGoal
        )

    val mealProgress =
        progress(
            mealCalories,
            mealGoal
        )

    val exerciseProgress =
        progress(
            exerciseMinutes,
            exerciseGoal
        )

    val sleepProgress =
        progress(
            sleepMinutes,
            sleepGoal
        )

    val overallProgress =
        (
                (
                        waterProgress +
                                mealProgress +
                                exerciseProgress +
                                sleepProgress
                        ) / 4f * 100f
                ).roundToInt()

    val hasData =
        waterIntake > 0 ||
                mealCalories > 0 ||
                exerciseMinutes > 0 ||
                sleepMinutes > 0

    val messageTitle =
        when {
            !hasData ->
                "Let's get started!"

            overallProgress < 40 ->
                "Good start!"

            overallProgress < 70 ->
                "Keep it going!"

            overallProgress < 100 ->
                "Great progress!"

            else ->
                "Goals completed!"
        }

    val messageText =
        when {
            !hasData ->
                "Log your first water, meal, exercise or sleep record to start tracking your health."

            overallProgress < 40 ->
                "You've started tracking today. Keep going and build up your healthy habits."

            overallProgress < 70 ->
                "You're making good progress today. Keep tracking your healthy habits."

            overallProgress < 100 ->
                "You're getting close to your daily goals. Keep up the good work!"

            else ->
                "Amazing work! You've reached your health goals for today."
        }

    ModalNavigationDrawer(
        drawerState =
            drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppNavigationDrawer(
                    userName =
                        userName,
                    profileImageUri =
                        profileImageUri,
                    onProfileClick = {
                        scope.launch {
                            drawerState.close()
                        }

                        onProfileClick()
                    },
                    onAchievementClick = {
                        scope.launch {
                            drawerState.close()
                        }

                        onAchievementClick()
                    },
                    onHealthGoalsClick = {
                        scope.launch {
                            drawerState.close()
                        }

                        onHealthGoalsClick()
                    },
                    onNotificationClick = {
                        scope.launch {
                            drawerState.close()
                        }

                        onNotificationClick()
                    },
                    onBmiClick = {
                        scope.launch {
                            drawerState.close()
                        }

                        onBmiClick()
                    },
                    onHelpClick = {
                        scope.launch {
                            drawerState.close()
                        }

                        onHelpClick()
                    },
                    onAboutClick = {
                        scope.launch {
                            drawerState.close()
                        }

                        onAboutClick()
                    },
                    onSignOutClick = {
                        scope.launch {
                            drawerState.close()
                        }

                        onSignOutClick()
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor =
                ScreenBg,
            bottomBar = {
                AppBottomNavigation(
                    selectedItem =
                        "Home",
                    onHomeClick = {},
                    onTrackersClick =
                        onTrackerClick,
                    onInsightClick =
                        onInsightClick
                )
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal =
                            20.dp
                    )
            ) {
                Spacer(
                    Modifier.height(18.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                "Hello, $userName",
                            fontSize = 24.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                TextDark
                        )

                        Text(
                            text =
                                if (hasData) {
                                    "Here's your progress today"
                                } else {
                                    "Start tracking your health today"
                                },
                            fontSize = 13.sp,
                            color =
                                TextMuted
                        )
                    }


                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription =
                                "Menu",
                            tint =
                                PrimaryGreen
                        )
                    }
                }

                Spacer(
                    Modifier.height(22.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(20.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        )
                ) {
                    Column(
                        modifier =
                            Modifier.padding(
                                18.dp
                            )
                    ) {
                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement
                                    .SpaceBetween,
                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Daily Overview",
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        TextDark
                                )

                                Text(
                                    if (hasData) {
                                        "$overallProgress% overall progress"
                                    } else {
                                        "No records yet"
                                    },
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,
                                    color =
                                        TextMuted
                                )
                            }

                            TextButton(
                                onClick =
                                    onNavigateToEditGoals
                            ) {
                                Text(
                                    "Edit Goals"
                                )
                            }
                        }

                        Spacer(
                            Modifier.height(16.dp)
                        )

                        HomeProgressRow(
                            icon =
                                Icons.Default.WaterDrop,
                            title = "Water",
                            value =
                                "$waterIntake / $waterGoal ml",
                            progress =
                                waterProgress,
                            iconBackground =
                                LightBlueBg,
                            iconColor =
                                WaterBlue
                        )

                        Spacer(
                            Modifier.height(14.dp)
                        )

                        HomeProgressRow(
                            icon =
                                Icons.Default.Restaurant,
                            title = "Calories",
                            value =
                                "$mealCalories / $mealGoal kcal",
                            progress =
                                mealProgress,
                            iconBackground =
                                LightOrangeBg,
                            iconColor =
                                MealOrange
                        )

                        Spacer(
                            Modifier.height(14.dp)
                        )

                        HomeProgressRow(
                            icon =
                                Icons.Default.FitnessCenter,
                            title = "Exercise",
                            value =
                                "${formatExerciseDuration(exerciseMinutes)} / $exerciseGoal mins",
                            progress =
                                exerciseProgress,
                            iconBackground =
                                LightGreenBg,
                            iconColor =
                                PrimaryGreen
                        )

                        Spacer(
                            Modifier.height(14.dp)
                        )

                        HomeProgressRow(
                            icon =
                                Icons.Default.NightsStay,
                            title = "Sleep",
                            value =
                                "${formatMinutes(sleepMinutes)} / ${formatMinutes(sleepGoal)}",
                            progress =
                                sleepProgress,
                            iconBackground =
                                LightPurpleBg,
                            iconColor =
                                SleepPurple
                        )
                    }
                }

                Spacer(
                    Modifier.height(22.dp)
                )

                Text(
                    text = "Quick Actions",
                    fontSize = 20.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        TextDark
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {
                    HomeQuickAction(
                        icon =
                            Icons.Default.WaterDrop,
                        title = "Water",
                        iconBackground =
                            LightBlueBg,
                        iconColor =
                            WaterBlue,
                        modifier =
                            Modifier.weight(1f),
                        onClick =
                            onNavigateToWaterTracker
                    )

                    HomeQuickAction(
                        icon =
                            Icons.Default.Restaurant,
                        title = "Meal",
                        iconBackground =
                            LightOrangeBg,
                        iconColor =
                            MealOrange,
                        modifier =
                            Modifier.weight(1f),
                        onClick =
                            onNavigateToMealTracker
                    )

                    HomeQuickAction(
                        icon =
                            Icons.Default.FitnessCenter,
                        title = "Exercise",
                        iconBackground =
                            LightGreenBg,
                        iconColor =
                            PrimaryGreen,
                        modifier =
                            Modifier.weight(1f),
                        onClick =
                            onNavigateToExerciseTracker
                    )

                    HomeQuickAction(
                        icon =
                            Icons.Default.NightsStay,
                        title = "Sleep",
                        iconBackground =
                            LightPurpleBg,
                        iconColor =
                            SleepPurple,
                        modifier =
                            Modifier.weight(1f),
                        onClick =
                            onNavigateToSleepTracker
                    )
                }

                Spacer(
                    Modifier.height(22.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(20.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                LightGreenBg
                        )
                ) {
                    Column(
                        modifier =
                            Modifier.padding(
                                18.dp
                            )
                    ) {
                        Text(
                            messageTitle,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                TextDark
                        )

                        Spacer(
                            Modifier.height(4.dp)
                        )

                        Text(
                            messageText,
                            color =
                                Color(0xFF35633E)
                        )
                    }
                }

                Spacer(
                    Modifier.height(28.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeProgressRow(
    icon: ImageVector,
    title: String,
    value: String,
    progress: Float,
    iconBackground: Color,
    iconColor: Color
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Surface(
            modifier =
                Modifier.size(44.dp),
            shape =
                RoundedCornerShape(14.dp),
            color =
                iconBackground
        ) {
            Box(
                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription =
                        title,
                    tint = iconColor
                )
            }
        }

        Spacer(
            Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text(
                    title,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        TextDark
                )

                Text(
                    value,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        TextMuted
                )
            }

            Spacer(
                Modifier.height(6.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress.coerceIn(
                        0f,
                        1f
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color =
                    iconColor
            )
        }
    }
}

@Composable
private fun HomeQuickAction(
    icon: ImageVector,
    title: String,
    iconBackground: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable {
                onClick()
            },
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {
        Column(
            modifier =
                Modifier.fillMaxSize(),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {
            Surface(
                modifier =
                    Modifier.size(40.dp),
                shape =
                    CircleShape,
                color =
                    iconBackground
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription =
                            title,
                        tint =
                            iconColor
                    )
                }
            }

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                title,
                fontSize = 11.sp,
                color =
                    TextDark
            )
        }
    }
}

private fun progress(
    current: Number,
    goal: Number
): Float {
    val curr =
        current.toFloat()

    val g =
        goal.toFloat()

    return if (g > 0f) {
        (
                curr /
                        g
                ).coerceIn(
                0f,
                1f
            )
    } else {
        0f
    }
}

private fun formatExerciseDuration(
    minutes: Double
): String {
    val totalSeconds = (minutes * 60).roundToInt()
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60

    return when {
        mins == 0 -> "${secs}s"
        secs == 0 -> "${mins}m"
        else -> "${mins}m ${secs}s"
    }
}

private fun formatMinutes(
    minutes: Int
): String {
    val hours =
        minutes / 60

    val remaining =
        minutes % 60

    return when {
        hours > 0 &&
                remaining > 0 ->
            "${hours}h ${remaining}m"

        hours > 0 ->
            "${hours}h"

        else ->
            "${remaining}m"
    }
}
