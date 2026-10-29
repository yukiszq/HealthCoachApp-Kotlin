package com.example.assignment.exercisescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.data.ActivityCategory
import com.example.assignment.data.ActivityRepository
import com.example.assignment.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectActivityScreen(
    onBack: () -> Unit,
    onActivitySelected: (String, Double?) -> Unit,
    onAddCustomClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedActivityForDistance by remember { mutableStateOf<ActivityCategory?>(null) }
    var distanceInputText by remember { mutableStateOf("") }
    var isDistanceError by remember { mutableStateOf(false) }

    fun isDistanceBasedActivity(name: String): Boolean {
        val q = name.lowercase()
        return q.contains("run") || q.contains("jog") || q.contains("walk") ||
                q.contains("hike") || q.contains("cycl") || q.contains("bike")
    }

    val filteredCategories = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            ActivityRepository.categories
        } else {
            ActivityRepository.categories.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    if (selectedActivityForDistance != null) {
        val activity = selectedActivityForDistance!!
        AlertDialog(
            onDismissRequest = {
                selectedActivityForDistance = null
                distanceInputText = ""
                isDistanceError = false
            },
            title = {
                Text(
                    text = "Enter Distance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkText
                )
            },
            text = {
                Column {
                    Text(
                        text = "Set target distance for ${activity.title} in kilometers (km):",
                        fontSize = 14.sp,
                        color = GrayText
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = distanceInputText,
                        onValueChange = {
                            distanceInputText = it
                            isDistanceError = false
                        },
                        label = { Text("Distance (km)") },
                        singleLine = true,
                        isError = isDistanceError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            focusedLabelColor = PrimaryGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isDistanceError) {
                        Text(
                            text = "Please enter a valid distance greater than 0",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val distance = distanceInputText.toDoubleOrNull()
                        if (distance != null && distance > 0.0) {
                            val actName = activity.title
                            selectedActivityForDistance = null
                            distanceInputText = ""
                            onActivitySelected(actName, distance)
                        } else {
                            isDistanceError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Start Workout", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedActivityForDistance = null
                        distanceInputText = ""
                        isDistanceError = false
                    }
                ) {
                    Text("Cancel", color = GrayText)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Activity",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCustomClick,
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Activity",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search activity...", color = GrayText) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = GrayText)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCategories) { category ->
                    val isOther = category.title.equals("Other", ignoreCase = true)

                    val cardContainerColor = if (isOther) LightGreenBg else Color(0xFFF9FBF9)
                    val iconBoxBgColor = if (isOther) PrimaryGreen else LightGreenBg
                    val iconTintColor = if (isOther) Color.White else PrimaryGreen

                    val icon = when {
                        isOther -> Icons.Default.Add
                        category.title.contains("Run", true) || category.title.contains("Jog", true) -> Icons.AutoMirrored.Filled.DirectionsRun
                        category.title.contains("Cycl", true) || category.title.contains("Bike", true) -> Icons.AutoMirrored.Filled.DirectionsBike
                        category.title.contains("Walk", true) -> Icons.AutoMirrored.Filled.DirectionsWalk
                        category.title.contains("Swim", true) -> Icons.Default.Pool
                        category.title.contains("Squash", true) || category.title.contains("Badminton", true) || category.title.contains("Tennis", true) || category.title.contains("Pickle", true) -> Icons.Default.SportsTennis
                        category.title.contains("Ping", true) || category.title.contains("Table Tennis", true) -> Icons.Default.SportsBaseball
                        category.title.contains("Yoga", true) || category.title.contains("Pilates", true) || category.title.contains("Stretch", true) -> Icons.Default.SelfImprovement
                        category.title.contains("Hike", true) -> Icons.Default.Hiking
                        category.title.contains("Basket", true) -> Icons.Default.SportsBasketball
                        category.title.contains("Box", true) || category.title.contains("Martial", true) -> Icons.Default.SportsMartialArts
                        category.title.contains("Foot", true) || category.title.contains("Soc", true) -> Icons.Default.SportsSoccer
                        category.title.contains("Volley", true) -> Icons.Default.SportsVolleyball
                        category.title.contains("Dance", true) || category.title.contains("Zumba", true) -> Icons.Default.SportsKabaddi
                        category.title.contains("Gym", true) || category.title.contains("Weight", true) || category.title.contains("Lift", true) -> Icons.Default.FitnessCenter
                        else -> Icons.Default.FitnessCenter
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isDistanceBasedActivity(category.title)) {
                                    selectedActivityForDistance = category
                                } else {
                                    onActivitySelected(category.title, null)
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(iconBoxBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconTintColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = category.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = category.subtitle,
                                fontSize = 12.sp,
                                color = GrayText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SelectActivityScreenPreview() {
    SelectActivityScreen(
        onBack = {},
        onActivitySelected = { _, _ -> },
        onAddCustomClick = {}
    )
}