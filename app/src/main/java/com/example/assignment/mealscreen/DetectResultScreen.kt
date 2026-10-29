package com.example.assignment.mealscreen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.assignment.model.MealEntry
import com.example.assignment.util.getCurrentDate
import com.example.assignment.util.getCurrentTime
import androidx.compose.ui.graphics.Color

private val ScreenBg = Color(0xFFF9F9FB)
private val PrimaryGreen = Color(0xFF0F6826)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectResultScreen(
    mealType: String,
    imageUri: String?,
    onSave: (MealEntry) -> Unit,
    onBack: () -> Unit
) {
    //Get the current Android context so app can access Android resources and functions
    val context = LocalContext.current

    var isLoading by remember {
        mutableStateOf(true)
    }

    var detectedFood by remember {
        mutableStateOf("Food")
    }

    var nutrition by remember {
        mutableStateOf(
            NutritionEstimate(
                calories = 0,
                protein = 0,
                carbs = 0,
                fat = 0,
                fiber = 0
            )
        )
    }
// Stores an error message if food detection fails
    var detectionError by remember {
        mutableStateOf<String?>(null)
    }

    val foodDetector = remember {
        FoodDetector()
    }
//Runs the food detection automatically when the imageUri is received
    LaunchedEffect(imageUri) {

        if (imageUri != null) {
    //call function to ends the selected image to FoodDetector for food recognition
            foodDetector.detectFood(
                context = context,
                imageUri = Uri.parse(imageUri),

                onResult = { label, _ ->

                    detectedFood = label
                    //Maps the detected food name to predefined estimated nutrition values
                    nutrition = getEstimatedNutrition(label)

                    detectionError = null
                    isLoading = false
                },

                onError = { error ->

                    detectedFood = "Food"
                    detectionError = error
                    isLoading = false
                }
            )

        } else {

            detectionError = "No image selected"
            isLoading = false
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meal Result",
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBg
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryGreen
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Spacer(Modifier.height(120.dp))
                CircularProgressIndicator(
                    color = PrimaryGreen
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Analyzing photo...",
                    color = TextDark
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    if (imageUri != null) {
                        // Displays the image selected by the user using its Uri
                        AsyncImage(
                            model = Uri.parse(imageUri),
                            contentDescription = "Selected meal image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No image selected",
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (nutrition.calories == 0) {
                                "No Food Detected"
                            } else {
                                "$detectedFood Detected"
                            },
                            color = TextDark
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryGreen
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White
                    )
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "ESTIMATED CONTENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                Text(
                    text = "${nutrition.calories} kcal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(Modifier.height(12.dp))

                ResultNutrientRow("Protein", nutrition.protein)
                Spacer(Modifier.height(8.dp))

                ResultNutrientRow("Carbs", nutrition.carbs)
                Spacer(Modifier.height(8.dp))

                ResultNutrientRow("Fat", nutrition.fat)
                Spacer(Modifier.height(8.dp))

                ResultNutrientRow("Dietary Fiber", nutrition.fiber)

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val entry = MealEntry(
                            id = 0,
                            mealType = mealType,
                            foodName = detectedFood,
                            kcal = nutrition.calories,
                            protein = nutrition.protein,
                            carbs = nutrition.carbs,
                            fat = nutrition.fat,
                            fiber = nutrition.fiber,
                            time = getCurrentTime(),
                            date = getCurrentDate(),
                            imageUri = imageUri ?: ""
                        )

                        onSave(entry)
                    },
                    enabled = nutrition.calories > 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Save Record")
                }
            }
        }
    }
}

@Composable
private fun ResultNutrientRow(
    label: String,
    value: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextDark
            )
            Text(
                text = "$value g",
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}
