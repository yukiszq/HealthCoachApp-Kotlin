package com.example.assignment.mealscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp

private val ScreenBg = Color(0xFFF9F9FB)
private val PrimaryGreen = Color(0xFF0F6826)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val MealLightBg = Color(0xFFFFF3E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogMealScreen(
    onBack: () -> Unit,
    onImageSelected: (String, Uri) -> Unit
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner")

    var selectedMealType by remember {
        mutableStateOf("Lunch")
    }

    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            //GetContent() lets user select existing content from the device
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            //Uri is the reference or location of the selected image
            if (uri != null) {
                onImageSelected(selectedMealType, uri)
            }
        }

    Scaffold(
        containerColor = ScreenBg,

        topBar = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp)
            ) {

                TopAppBar(
                    title = {
                        Text(
                            text = "Log Meal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    },

                    navigationIcon = {

                        IconButton(
                            onClick = onBack
                        ) {

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PrimaryGreen
                            )
                        }
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ScreenBg
                    )
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFE5E5E5)
                )
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // Meal Type
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Text(
                        text = "Meal Type",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = {
                            dropdownExpanded = it
                        }
                    ) {

                        OutlinedTextField(
                            value = selectedMealType,
                            onValueChange = {},
                            readOnly = true,

                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = dropdownExpanded
                                )
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),

                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,

                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,

                                focusedLabelColor = PrimaryGreen,
                                unfocusedLabelColor = TextMuted,

                                focusedBorderColor = PrimaryGreen,
                                unfocusedBorderColor = Color(0xFFD0D0D0),

                                focusedTrailingIconColor = PrimaryGreen,
                                unfocusedTrailingIconColor = TextDark,

                                cursorColor = PrimaryGreen
                            )

                        )

                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = {
                                dropdownExpanded = false
                            },
                            containerColor = Color.White
                        ) {

                            mealTypes.forEach { type ->

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = type,
                                            color = TextDark
                                        )
                                    },

                                    onClick = {
                                        selectedMealType = type
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Upload Meal Photo
            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor = MealLightBg
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Surface(
                        modifier = Modifier.size(68.dp),
                        shape = RoundedCornerShape(34.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = "Upload your meal photo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Select a picture of your food for nutrition analysis.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Select Image",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
