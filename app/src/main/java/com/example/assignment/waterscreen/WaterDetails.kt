package com.example.assignment.waterscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.model.WaterRecord

private val PrimaryGreen = Color(0xFF0F6826)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)

private val WaterBlue = Color(0xFF1976D2)
private val LightBlueBg = Color(0xFFEAF3FF)
private val DeleteRed = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterDetail(
    waterRecord: WaterRecord,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    var showDeleteDialog by remember {
        mutableStateOf(false)
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
                            text = "Water Details",
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
        },

        bottomBar = {

            Surface(
                color = ScreenBg,
                shadowElevation = 6.dp
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 14.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    Button(
                        onClick = onEdit,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = RoundedCornerShape(18.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Edit",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            showDeleteDialog = true
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = RoundedCornerShape(18.dp),

                        colors = ButtonDefaults.buttonColors(
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
                            text = "Delete",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "WATER INTAKE",
                fontSize = 11.sp,
                color = TextMuted
            )

            Text(
                text = "Water Record",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor = LightBlueBg
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 30.dp
                        ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 3.dp
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
                                    Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = "WATER CONSUMED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = WaterBlue
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.Bottom
                    ) {

                        Text(
                            text =
                                "${waterRecord.intakeAmount}",

                            fontSize = 38.sp,
                            fontWeight = FontWeight.Medium,
                            color = WaterBlue
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )

                        Text(
                            text = waterRecord.unit,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            color = WaterBlue,

                            modifier =
                                Modifier.padding(bottom = 5.dp)
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = LightBlueBg
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Schedule,

                                contentDescription = null,
                                tint = WaterBlue,

                                modifier =
                                    Modifier.size(21.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    Column {

                        Text(
                            text = "Logged At",
                            fontSize = 12.sp,
                            color = TextMuted
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = waterRecord.loggedAt,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(120.dp)
            )
        }
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            shape = RoundedCornerShape(20.dp),

            containerColor = Color.White,

            icon = {

                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color(0xFFFFEBEE)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Delete,

                            contentDescription = null,
                            tint = DeleteRed
                        )
                    }
                }
            },

            title = {

                Text(
                    text = "Delete Water Record?",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },

            text = {

                Text(
                    text = "This water record will be permanently deleted.",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        showDeleteDialog = false
                        onDelete()
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeleteRed,
                        contentColor = Color.White
                    ),

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {

                    Text(
                        text = "Cancel",
                        color = TextDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun WaterDetailScreenPreview() {

    MaterialTheme {

        WaterDetail(
            waterRecord = WaterRecord(
                waterId = 1,
                userId = 1,
                intakeAmount = 500,
                unit = "ml",
                loggedAt = "2026-07-12 10:45 AM"
            ),

            onBack = {},
            onEdit = {},
            onDelete = {}
        )
    }
}