package com.example.assignment.exercisescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.data.ActivityRepository
import com.example.assignment.data.UserData
import com.example.assignment.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatingSessionScreen(
    activityName: String,
    userEnteredDistanceKm: Double? = null,
    onBack: () -> Unit,
    onClick: (Long, Int, Double?) -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var caloriesBurned by remember { mutableDoubleStateOf(0.0) }

    val metValue = remember(activityName) {
        ActivityRepository.getMetForActivity(activityName)
    }

    // Use user's weight if entered, otherwise default to 70kg
    val userWeight = remember {
        val weight = UserData.currentUser?.weight ?: 0.0
        if (weight > 0) weight else ActivityRepository.USER_WEIGHT_KG
    }

    // Burn rate in kcal/min based on formula (MET * 3.5 * weight) / 200
    val burnRatePerMin = remember(metValue, userWeight) {
        (metValue * 3.5 * userWeight) / 200.0
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            elapsedSeconds++
            val minutesDouble = elapsedSeconds / 60.0
            caloriesBurned = burnRatePerMin * minutesDouble
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Health Coach+",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF108A43)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFECEFF1),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = DarkText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF8FC))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBF8FC))
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = activityName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRunning) "Workout active..." else "Tap Start to begin tracking",
                    fontSize = 14.sp,
                    color = GrayText
                )
            }

            // Central Circular Timer Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(230.dp)
                    .background(Color.White, CircleShape)
                    .border(10.dp, PrimaryGreen, CircleShape)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = "DYNAMIC DURATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrayText,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // MET Badge Label
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF00C853),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(Locale.US, "MET %.1f", metValue),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00C853)
                            )
                        }
                    }
                }
            }

            // Metric Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Burn Rate Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F7))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "BURN RATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrayText
                        )
                        Column {
                            Text(
                                text = String.format(Locale.US, "%.1f", burnRatePerMin),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "kcal/min",
                                fontSize = 11.sp,
                                color = GrayText
                            )
                        }
                    }
                }

                // Total Calories Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F7))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TOTAL CALORIES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrayText
                        )
                        Column {
                            Text(
                                text = "${caloriesBurned.toInt()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "kcal",
                                fontSize = 11.sp,
                                color = GrayText
                            )
                        }
                    }
                }

                // Distance Card
                userEnteredDistanceKm?.let { dist ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F7))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "DISTANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrayText
                            )
                            Column {
                                Text(
                                    text = String.format(Locale.US, "%.2f", dist),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(
                                    text = "km",
                                    fontSize = 11.sp,
                                    color = GrayText
                                )
                            }
                        }
                    }
                }
            }

            // Control Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start / Pause Button
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) Color(0xFFE67E22) else PrimaryGreen
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRunning) "Pause Workout" else "Start Workout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // End & Save Button
                val isEndActive = elapsedSeconds > 0
                val endButtonBgColor = if (isEndActive) Color(0xFFE53935) else Color(0xFFF5F5F7)
                val endButtonTextColor = if (isEndActive) Color.White else Color(0xFFB0BEC5)
                val endButtonBorderColor = if (isEndActive) Color(0xFFE53935) else Color(0xFFCFD8DC)

                OutlinedButton(
                    onClick = {
                        if (isEndActive) {
                            isRunning = false
                            onClick(elapsedSeconds, caloriesBurned.toInt(), userEnteredDistanceKm)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = endButtonBgColor,
                        contentColor = endButtonTextColor
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, endButtonBorderColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = endButtonTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "End & Save Workout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = endButtonTextColor
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HealthCoachWithDistancePreview() {
    CalculatingSessionScreen(
        activityName = "Running",
        userEnteredDistanceKm = 5.2,
        onBack = {},
        onClick = { _, _, _ -> }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HealthCoachWithoutDistancePreview() {
    CalculatingSessionScreen(
        activityName = "Gym",
        userEnteredDistanceKm = null,
        onBack = {},
        onClick = { _, _, _ -> }
    )
}