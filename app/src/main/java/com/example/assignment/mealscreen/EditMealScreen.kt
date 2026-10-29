package com.example.assignment.mealscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.model.MealEntry
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ScreenBg = Color(0xFFF9F9FB)
private val PrimaryGreen = Color(0xFF0F6826)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMealScreen(
    meal: MealEntry,
    onBack: () -> Unit,
    onSave: (MealEntry) -> Unit
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextDark,
        unfocusedTextColor = TextDark,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedLabelColor = PrimaryGreen,
        unfocusedLabelColor = TextMuted,
        focusedBorderColor = PrimaryGreen,
        unfocusedBorderColor = Color(0xFFD0D0D0),
        cursorColor = PrimaryGreen
    )
    var mealType by remember {
        mutableStateOf(meal.mealType)
    }

    var foodName by remember {
        mutableStateOf(meal.foodName)
    }

    var kcal by remember {
        mutableStateOf(meal.kcal.toString())
    }

    var protein by remember {
        mutableStateOf(meal.protein.toString())
    }

    var carbs by remember {
        mutableStateOf(meal.carbs.toString())
    }

    var fat by remember {
        mutableStateOf(meal.fat.toString())
    }

    var fiber by remember {
        mutableStateOf(meal.fiber.toString())
    }

    var notes by remember {
        mutableStateOf(meal.notes)
    }

    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Meal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBg
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // =========================
            // MEAL TYPE
            // =========================

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = {
                    dropdownExpanded = it
                }
            ) {

                OutlinedTextField(
                    value = mealType,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = "Meal Type",
                            color = TextMuted
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = dropdownExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = textFieldColors
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = {
                        dropdownExpanded = false
                    }
                ) {

                    listOf(
                        "Breakfast",
                        "Lunch",
                        "Dinner"
                    ).forEach { type ->

                        DropdownMenuItem(
                            text = {
                                Text(type)
                            },
                            onClick = {
                                mealType = type
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // =========================
            // FOOD NAME
            // =========================

            OutlinedTextField(
                value = foodName,
                onValueChange = {
                    foodName = it
                },
                label = {
                    Text(
                        text = "Food Name",
                        color = TextMuted
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )


            // =========================
            // CALORIES
            // =========================

            OutlinedTextField(
                value = kcal,
                onValueChange = {
                    kcal = it
                },
                label = {
                    Text("Calories (kcal)")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )

            // =========================
            // PROTEIN
            // =========================

            OutlinedTextField(
                value = protein,
                onValueChange = {
                    protein = it
                },
                label = {
                    Text("Protein (g)")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )

            // =========================
            // CARBS
            // =========================

            OutlinedTextField(
                value = carbs,
                onValueChange = {
                    carbs = it
                },
                label = {
                    Text("Carbs (g)")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )

            // =========================
            // FAT
            // =========================

            OutlinedTextField(
                value = fat,
                onValueChange = {
                    fat = it
                },
                label = {
                    Text("Fat (g)")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )

            // =========================
            // FIBER
            // =========================
            OutlinedTextField(
                value = fiber,
                onValueChange = {
                    fiber = it
                },
                label = {
                    Text("Fiber (g)")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )

            // =========================
            // NOTES
            // =========================

            OutlinedTextField(
                value = notes,
                onValueChange = {
                    notes = it
                },
                label = {
                    Text("Personal Notes")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = textFieldColors
            )

            Spacer(Modifier.height(8.dp))

            // =========================
            // SAVE CHANGES
            // =========================

            Button(
                onClick = {
                    val updatedMeal = meal.copy(
                        mealType = mealType,
                        foodName = foodName,
                        kcal = kcal.toIntOrNull() ?: meal.kcal,
                        protein = protein.toIntOrNull() ?: meal.protein,
                        carbs = carbs.toIntOrNull() ?: meal.carbs,
                        fat = fat.toIntOrNull() ?: meal.fat,
                        fiber = fiber.toIntOrNull() ?: meal.fiber,
                        notes = notes
                    )

                    onSave(updatedMeal)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Save Changes")
            }

            // =========================
            // CANCEL
            // =========================

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryGreen
                )
            ) {
                Text("Cancel")
            }
        }
    }
}