package com.example.assignment.sleeptrackerscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NightsStay
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
import com.example.assignment.model.SleepRecord
import java.text.SimpleDateFormat
import java.util.Locale


// same colors with other tracker screens
private val PrimaryGreen = Color(0xFF0F6826)
private val LightGreenBg = Color(0xFFE8F5E9)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val CardBg = Color(0xFFF2F2F7)
private val DeleteRed = Color(0xFFD32F2F)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepDetail(
    sleepRecord: SleepRecord,
    goalMinutes: Int = 480,
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
                            text = "Sleep Details",
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
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // record date
            Text(
                text = "DATE OF ENTRY",
                fontSize = 11.sp,
                color = TextMuted
            )


            Text(
                text = formatDetailDate(
                    sleepRecord.date
                ),

                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )


            // total sleep duration
            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor = LightGreenBg
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(26.dp)
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Text(
                        text = "TOTAL SLEEP DURATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen
                    )


                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )


                    Text(
                        text = formatSleepDetailDuration(
                            sleepRecord.durationMinutes
                        ),

                        fontSize = 36.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen
                    )
                }
            }


            // sleep information
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

                    SleepDetailRow(
                        label = "Sleep Type",
                        value = sleepRecord.sleepType
                    )


                    HorizontalDivider(
                        modifier = Modifier.padding(
                            vertical = 10.dp
                        ),
                        color = Color(0xFFE5E5E5)
                    )


                    SleepDetailRow(
                        label = "Bedtime",
                        value = sleepRecord.bedtime
                    )


                    HorizontalDivider(
                        modifier = Modifier.padding(
                            vertical = 10.dp
                        ),
                        color = Color(0xFFE5E5E5)
                    )


                    SleepDetailRow(
                        label = "Wake-up Time",
                        value = sleepRecord.wakeUpTime
                    )
                }
            }


            // sleep quality and goal
            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                SleepInfoCard(
                    title = "Sleep Quality",

                    value = getDetailQuality(
                        sleepRecord.quality
                    ),

                    modifier =
                        Modifier.weight(1f)
                )


                SleepInfoCard(
                    title = "Duration",
                    value = formatSleepDetailDuration(sleepRecord.durationMinutes),
                    modifier = Modifier.weight(1f)
                )
            }
            // simple insight based on this sleep record
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightGreenBg
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Sleep Insight",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = getSleepInsight(
                            sleepType = sleepRecord.sleepType,
                            durationMinutes = sleepRecord.durationMinutes,
                            quality = sleepRecord.quality,
                            goalMinutes = goalMinutes
                        ),
                        fontSize = 14.sp,
                        color = TextDark,
                        lineHeight = 20.sp
                    )
                }
            }


            // only show journal if user wrote something
            if (sleepRecord.notes.isNotBlank()) {

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
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Journal Entry",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Text(
                            text = sleepRecord.notes,
                            fontSize = 14.sp,
                            color = TextDark,
                            lineHeight = 21.sp
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(130.dp)
            )
        }
    }


    // ask user first before delete
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
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFFFEBEE)
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = DeleteRed
                        )
                    }
                }
            },

            title = {

                Text(
                    text = "Delete Sleep Record?",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },

            text = {

                Text(
                    text = "This sleep record will be permanently deleted.",
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



@Composable
private fun SleepDetailRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = TextMuted
        )


        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
    }
}



@Composable
private fun SleepInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = CardBg
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                fontSize = 11.sp,
                color = TextMuted
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}



// show hour and minute
private fun formatSleepDetailDuration(
    minutes: Int
): String {

    val hour = minutes / 60
    val minute = minutes % 60

    return when {

        hour > 0 && minute > 0 -> {
            "${hour}h ${minute}m"
        }

        hour > 0 -> {
            "${hour}h"
        }

        else -> {
            "${minute}m"
        }
    }
}



// change quality number to text
private fun getDetailQuality(
    quality: Int
): String {

    return when (quality) {

        1 -> "Poor"

        2 -> "Fair"

        3 -> "Good"

        4 -> "Very Good"

        5 -> "Excellent"

        else -> "Not Rated"
    }
}



// show date like 12 July 2026
private fun formatDetailDate(
    date: String
): String {

    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        val output =
            SimpleDateFormat(
                "d MMMM yyyy",
                Locale.US
            )


        output.format(
            input.parse(date)!!
        )

    } catch (e: Exception) {

        date
    }
}

private fun getSleepInsight(
    sleepType: String,
    durationMinutes: Int,
    quality: Int,
    goalMinutes: Int
): String {

    val duration = formatSleepDetailDuration(durationMinutes)
    val qualityText = getDetailQuality(quality)

    return if (sleepType == "Quick Nap") {

        when {
            durationMinutes <= 30 ->
                "You had a short $duration nap. Your sleep quality was rated $qualityText."

            durationMinutes <= 60 ->
                "You had a $duration nap. Your sleep quality was rated $qualityText."

            else ->
                "You had a longer nap of $duration. Your sleep quality was rated $qualityText."
        }

    } else {

        when {
            durationMinutes < goalMinutes / 2 ->
                "You slept for $duration, which is quite below your sleep goal. Try to allow more time for rest. Your sleep quality was rated $qualityText."

            durationMinutes < goalMinutes ->
                "You slept for $duration and are getting closer to your sleep goal. Your sleep quality was rated $qualityText."

            else ->
                "You reached your sleep goal with $duration of sleep. Your sleep quality was rated $qualityText."
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SleepDetailScreenPreview() {

    MaterialTheme {

        SleepDetail(

            sleepRecord = SleepRecord(
                id = 1,
                date = "2026-07-12",
                sleepType = "Night Sleep",
                bedtime = "11:30 PM",
                wakeUpTime = "7:00 AM",
                durationMinutes = 450,
                quality = 3,
                notes = "I felt very refreshed this morning. Avoiding blue light before bed helped me fall asleep faster."
            ),

            onBack = {},
            onEdit = {},
            onDelete = {}
        )
    }
}