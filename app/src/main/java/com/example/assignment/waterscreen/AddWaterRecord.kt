package com.example.assignment.waterscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.model.WaterRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val PrimaryGreen = Color(0xFF0F6826)
private val LightGreenBg = Color(0xFFE8F5E9)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)

private val WaterBlue = Color(0xFF1976D2)
private val LightBlueBg = Color(0xFFEAF3FF)
private val WaterFillBlue = Color(0xFF64A5F5)
private val DeleteRed = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWaterRecord(
    currentIntake: Int = 1200,
    goalAmount: Int = 2000,
    userId: Int = 1,
    existingRecord: WaterRecord? = null,
    onBack: () -> Unit,
    onSave: (WaterRecord) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var selectedAmount by remember(existingRecord) {
        mutableIntStateOf(
            existingRecord?.intakeAmount ?: 250
        )
    }

    val intakeWithoutOldRecord =
        if (existingRecord != null) {
            (currentIntake - existingRecord.intakeAmount)
                .coerceAtLeast(0)
        } else {
            currentIntake
        }

    val previewIntake =
        intakeWithoutOldRecord + selectedAmount

    val progress = if (goalAmount > 0) {
        (previewIntake.toFloat() / goalAmount)
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    val progressPercent =
        (progress * 100).toInt()

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
                            text = if (existingRecord == null) {
                                "Log Water Intake"
                            } else {
                                "Edit Water Intake"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    },

                    navigationIcon = {
                        IconButton(
                            onClick = onBack
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
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

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor = LightBlueBg
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Surface(
                        modifier = Modifier.size(68.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.WaterDrop,

                                contentDescription = null,
                                tint = WaterBlue,

                                modifier =
                                    Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.Bottom
                    ) {

                        Text(
                            text = "$previewIntake",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )

                        Text(
                            text = "ml",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,

                            modifier =
                                Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Text(
                        text = "of $goalAmount ml Goal",
                        fontSize = 13.sp,
                        color = TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    LinearProgressIndicator(
                        progress = {
                            progress
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),

                        color = WaterFillBlue,

                        trackColor =
                            Color(0xFFD8E8FA)
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White
                    ) {

                        Text(
                            text = "$progressPercent%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue,

                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            )
                        )
                    }
                }
            }

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
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = if (existingRecord == null) {
                            "Add Consumption"
                        } else {
                            "Update Consumption"
                        },

                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier
                                .size(54.dp)
                                .clickable {
                                    if (selectedAmount > 50) {
                                        selectedAmount -= 50
                                    }
                                },

                            shape = CircleShape,
                            color = Color(0xFFF2F2F2)
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Remove,

                                    contentDescription =
                                        "Decrease",

                                    tint = PrimaryGreen
                                )
                            }
                        }

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "$selectedAmount",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Text(
                                text = "milliliters",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .size(54.dp)
                                .clickable {
                                    selectedAmount += 50
                                },

                            shape = CircleShape,
                            color = Color(0xFFF2F2F2)
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Add,

                                    contentDescription =
                                        "Increase",

                                    tint = PrimaryGreen
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.Center
                    ) {

                        WaterAmountChip(
                            text = "100ml",
                            selected = selectedAmount == 100,

                            onClick = {
                                selectedAmount = 100
                            }
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        WaterAmountChip(
                            text = "250ml",
                            selected = selectedAmount == 250,

                            onClick = {
                                selectedAmount = 250
                            }
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        WaterAmountChip(
                            text = "500ml",
                            selected = selectedAmount == 500,

                            onClick = {
                                selectedAmount = 500
                            }
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Button(
                        onClick = {

                            onSave(
                                WaterRecord(
                                    waterId =
                                        existingRecord?.waterId
                                            ?: System.currentTimeMillis()
                                                .toInt(),

                                    userId =
                                        existingRecord?.userId
                                            ?: userId,

                                    intakeAmount =
                                        selectedAmount,

                                    unit =
                                        existingRecord?.unit
                                            ?: "ml",

                                    loggedAt =
                                        existingRecord?.loggedAt
                                            ?: getCurrentWaterDateTime()
                                )
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape =
                            RoundedCornerShape(18.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    PrimaryGreen,

                                contentColor =
                                    Color.White
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.CheckCircle,

                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = if (existingRecord == null) {
                                "Save Record"
                            } else {
                                "Update Record"
                            },

                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (existingRecord != null && onDelete != null) {

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Button(
                            onClick = {
                                showDeleteDialog = true
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),

                            shape =
                                RoundedCornerShape(18.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = DeleteRed,
                                    contentColor = Color.White
                                )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = "Delete Record",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        Color(0xFFB7E4C0)
                )
            ) {

                Row(
                    modifier = Modifier.padding(18.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier.size(44.dp),

                        shape =
                            RoundedCornerShape(14.dp),

                        color =
                            Color(0xFFD8F2DE)
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.WaterDrop,

                                contentDescription = null,
                                tint = Color.White,

                                modifier =
                                    Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    Column {

                        Text(
                            text = "Stay Sharp",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = "Proper hydration improves focus and energy levels throughout your workday.",
                            fontSize = 12.sp,
                            color = Color(0xFF35633E),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text(
                    text = "Delete Water Record?",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text = "This water record will be permanently deleted."
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete?.invoke()
                    },

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = DeleteRed
                        )
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun WaterAmountChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(20.dp),

        color = if (selected) {
            LightGreenBg
        } else {
            Color(0xFFEAF3FF)
        }
    ) {

        Text(
            text = text,

            fontSize = 12.sp,

            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },

            color = if (selected) {
                PrimaryGreen
            } else {
                WaterBlue
            },

            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 8.dp
            )
        )
    }
}

private fun getCurrentWaterDateTime(): String {

    val format =
        SimpleDateFormat(
            "yyyy-MM-dd h:mm a",
            Locale.US
        )

    return format.format(
        Calendar.getInstance().time
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Log Water"
)
@Composable
private fun AddWaterRecordScreenPreview() {

    MaterialTheme {

        AddWaterRecord(
            currentIntake = 1200,
            goalAmount = 2000,
            userId = 1,
            onBack = {},
            onSave = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Edit Water"
)
@Composable
private fun EditWaterRecordScreenPreview() {

    MaterialTheme {

        AddWaterRecord(
            currentIntake = 1200,
            goalAmount = 2000,

            existingRecord = WaterRecord(
                waterId = 1,
                userId = 1,
                intakeAmount = 500,
                unit = "ml",
                loggedAt = "2026-07-12 10:45 AM"
            ),

            onBack = {},
            onSave = {},
            onDelete = {}
        )
    }
}