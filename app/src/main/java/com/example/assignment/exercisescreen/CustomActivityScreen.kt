package com.example.assignment.exercisescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.assignment.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomActivityScreen(
    onBack: () -> Unit,
    onStartWorkout: (String) -> Unit
) {
    var activityName by remember { mutableStateOf("") }
    val popularActivities = listOf("Pickleball", "Yoga", "Pilates", "Stretching")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Other Activity",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryGreen
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Custom Workout", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text("Enter or choose an activity name to track", fontSize = 14.sp, color = GrayText)

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = activityName,
                    onValueChange = { activityName = it },
                    label = { Text("Activity Name") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryGreen) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreenBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recommended Baseline Standards",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            BaselineRow(
                                category = "Light (Yoga/Stretching):",
                                detail = "30–45 mins (~150 kcal)"
                            )
                            BaselineRow(
                                category = "Moderate (Brisk Walk):",
                                detail = "30–60 mins (~250 kcal)"
                            )
                            BaselineRow(
                                category = "Vigorous (Running/HIIT):",
                                detail = "20–40 mins (~400+ kcal)"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Popular Quick Choices", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GrayText)
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(popularActivities) { suggestion ->
                        FilterChip(
                            selected = activityName == suggestion,
                            onClick = { activityName = suggestion },
                            label = { Text(suggestion) }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val finalActivityName = activityName.ifBlank { "Custom Activity" }
                    ActivityRepository.addCustomActivity(finalActivityName)
                    onStartWorkout(finalActivityName)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Tracking", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun BaselineRow(category: String, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = category, fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.Medium)
        Text(text = detail, fontSize = 12.sp, color = GrayText)
    }
}

// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CustomActivityScreenPreview() {
    CustomActivityScreen(
        onBack = {},
        onStartWorkout = {}
    )
}