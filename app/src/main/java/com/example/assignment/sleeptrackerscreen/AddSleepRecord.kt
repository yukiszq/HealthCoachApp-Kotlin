package com.example.assignment.sleeptrackerscreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.model.SleepRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// same colors used in tracker screens
private val PrimaryGreen = Color(0xFF0F6826)
private val LightGreenBg = Color(0xFFE8F5E9)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val CardBg = Color(0xFFF2F2F7)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSleepRecordScreen(
    selectedDate: String = "2026-07-12",
    existingRecord: SleepRecord? = null,
    existingRecords: List<SleepRecord> = emptyList(),
    onBack: () -> Unit,
    onSave: (SleepRecord) -> Unit
) {

    val context = LocalContext.current

    var date by remember(existingRecord) {
        mutableStateOf(
            existingRecord?.date ?: selectedDate
        )
    }

    var sleepType by remember(existingRecord) {
        mutableStateOf(
            existingRecord?.sleepType ?: "Night Sleep"
        )
    }

    var sleepTime by remember(existingRecord) {
        mutableStateOf(
            existingRecord?.bedtime ?: "11:30 PM"
        )
    }

    var wakeUpTime by remember(existingRecord) {
        mutableStateOf(
            existingRecord?.wakeUpTime ?: "7:00 AM"
        )
    }

    var sleepQuality by remember(existingRecord) {
        mutableIntStateOf(
            existingRecord?.quality ?: 3
        )
    }

    var notes by remember(existingRecord) {
        mutableStateOf(
            existingRecord?.notes ?: ""
        )
    }

    var errorMessage by remember {
        mutableStateOf("")
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
                            text = if (existingRecord == null) {
                                "Add Sleep Record"
                            } else {
                                "Edit Sleep Record"
                            },
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

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            // choose date
            SleepInputCard {

                Text(
                    text = "Date",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            showSleepDatePicker(
                                currentDate = date,
                                context = context,

                                onDateSelected = {
                                    date = it
                                }
                            )
                        },

                    shape = RoundedCornerShape(14.dp),

                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.White
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = formatSleepDate(date),
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            color = TextDark
                        )

                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Choose Date",
                            tint = PrimaryGreen
                        )
                    }
                }
            }


            // night sleep or nap
            SleepInputCard {

                Text(
                    text = "Sleep Type",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                SleepTypeOption(
                    text = "Night Sleep",
                    selected = sleepType == "Night Sleep",

                    icon = {

                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = PrimaryGreen
                        )
                    },

                    onClick = {
                        sleepType = "Night Sleep"
                    }
                )


                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                SleepTypeOption(
                    text = "Quick Nap",
                    selected = sleepType == "Quick Nap",

                    icon = {

                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = PrimaryGreen
                        )
                    },

                    onClick = {
                        sleepType = "Quick Nap"
                    }
                )
            }


            // sleep and wake up time
            SleepInputCard {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {


                    SleepTimeBox(
                        label = "Sleep Time",
                        value = sleepTime,
                        modifier = Modifier.weight(1f),

                        onClick = {

                            showSleepTimePicker(
                                context = context,
                                currentTime = sleepTime,

                                onTimeSelected = {
                                    sleepTime = it
                                }
                            )
                        }
                    )


                    SleepTimeBox(
                        label = "Wake-up Time",
                        value = wakeUpTime,
                        modifier = Modifier.weight(1f),

                        onClick = {

                            showSleepTimePicker(
                                context = context,
                                currentTime = wakeUpTime,

                                onTimeSelected = {
                                    wakeUpTime = it
                                }
                            )
                        }
                    )
                }
            }


            // rate sleep quality
            SleepInputCard {

                Text(
                    text = "Sleep Quality",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    SleepQualityOption(
                        emoji = "😞",
                        label = "Poor",
                        selected = sleepQuality == 1,
                        modifier = Modifier.weight(1f),

                        onClick = {
                            sleepQuality = 1
                        }
                    )


                    SleepQualityOption(
                        emoji = "😐",
                        label = "Fair",
                        selected = sleepQuality == 2,
                        modifier = Modifier.weight(1f),

                        onClick = {
                            sleepQuality = 2
                        }
                    )


                    SleepQualityOption(
                        emoji = "🙂",
                        label = "Good",
                        selected = sleepQuality == 3,
                        modifier = Modifier.weight(1f),

                        onClick = {
                            sleepQuality = 3
                        }
                    )


                    SleepQualityOption(
                        emoji = "😊",
                        label = "Very Good",
                        selected = sleepQuality == 4,
                        modifier = Modifier.weight(1f),

                        onClick = {
                            sleepQuality = 4
                        }
                    )
                }
            }


            // notes can be empty
            SleepInputCard {

                Text(
                    text = "Notes (Optional)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                OutlinedTextField(
                    value = notes,

                    onValueChange = {
                        notes = it
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),

                    placeholder = {

                        Text(
                            text = "Add any notes about your sleep...",
                            color = TextMuted
                        )
                    },

                    maxLines = 5,

                    shape = RoundedCornerShape(14.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        cursorColor = PrimaryGreen
                    )
                )
            }


            if (errorMessage.isNotEmpty()) {

                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }


            Button(
                onClick = {

                    val durationMinutes =
                        calculateSleepMinutes(
                            sleepTime = sleepTime,
                            wakeUpTime = wakeUpTime
                        )


                    if (durationMinutes <= 0) {
                        errorMessage = "Please select a valid sleep and wake-up time."

                    } else if (sleepType == "Quick Nap" && durationMinutes > 180) {
                        errorMessage = "Quick Nap cannot be longer than 3 hours."

                    } else {
                        // dont compare with itself when editing
                        val otherRecords = existingRecords.filter {
                            it.id != existingRecord?.id && it.date == date
                        }

                        val duplicate = otherRecords.any {
                            it.sleepType == sleepType &&
                                    it.bedtime == sleepTime &&
                                    it.wakeUpTime == wakeUpTime
                        }

                        val overlap = otherRecords.any {
                            isSleepTimeOverlap(
                                sleepTime,
                                wakeUpTime,
                                it.bedtime,
                                it.wakeUpTime
                            )
                        }

                        when {
                            duplicate -> {
                                errorMessage = "This sleep record already exists."
                            }

                            overlap -> {
                                errorMessage = "This sleep time overlaps with another record."
                            }

                            else -> {
                                errorMessage = ""

                                onSave(
                                    SleepRecord(
                                        // keep old id when editing
                                        id = existingRecord?.id ?: System.currentTimeMillis().toInt(),
                                        date = date,
                                        sleepType = sleepType,
                                        bedtime = sleepTime,
                                        wakeUpTime = wakeUpTime,
                                        durationMinutes = durationMinutes,
                                        quality = sleepQuality,
                                        notes = notes
                                    )
                                )
                            }
                        }
                    }
                },

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
                    imageVector = Icons.Default.CheckCircle,
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


            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }
}



@Composable
private fun SleepInputCard(
    content: @Composable ColumnScope.() -> Unit
) {

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
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}



@Composable
private fun SleepTypeOption(
    text: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(14.dp),

        colors = CardDefaults.outlinedCardColors(
            containerColor = if (selected) {
                LightGreenBg
            } else {
                Color.White
            }
        ),

        border = BorderStroke(
            width = if (selected) {
                2.dp
            } else {
                1.dp
            },

            color = if (selected) {
                PrimaryGreen
            } else {
                Color(0xFFE0E0E0)
            }
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            icon()

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )

            RadioButton(
                selected = selected,
                onClick = onClick,

                colors = RadioButtonDefaults.colors(
                    selectedColor = PrimaryGreen
                )
            )
        }
    }
}



@Composable
private fun SleepTimeBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Column(
        modifier = modifier
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )


        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

            shape = RoundedCornerShape(14.dp),

            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.White
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 16.dp
                    ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = value,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}



@Composable
private fun SleepQualityOption(
    emoji: String,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier
            .height(76.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(14.dp),

        color = if (selected) {
            PrimaryGreen
        } else {
            CardBg
        }
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = emoji,
                fontSize = 20.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = label,

                fontSize = if (label == "Very Good") {
                    9.sp
                } else {
                    10.sp
                },

                fontWeight = FontWeight.Medium,

                color = if (selected) {
                    Color.White
                } else {
                    TextDark
                }
            )
        }
    }
}



// calculate the duration, also handle sleep after midnight
private fun calculateSleepMinutes(
    sleepTime: String,
    wakeUpTime: String
): Int {

    return try {

        val format =
            SimpleDateFormat(
                "h:mm a",
                Locale.US
            )

        val start =
            format.parse(sleepTime)
                ?: return 0

        val end =
            format.parse(wakeUpTime)
                ?: return 0


        var difference =
            end.time - start.time


        if (difference <= 0) {

            difference +=
                24 * 60 * 60 * 1000L
        }


        (difference / 60000).toInt()

    } catch (e: Exception) {

        0
    }
}
private fun timeToMinutes(time: String): Int? {
    return try {
        val format = SimpleDateFormat("h:mm a", Locale.US)
        val parsed = format.parse(time) ?: return null
        val calendar = Calendar.getInstance()
        calendar.time = parsed

        calendar.get(Calendar.HOUR_OF_DAY) * 60 +
                calendar.get(Calendar.MINUTE)
    } catch (_: Exception) {
        null
    }
}

private fun isSleepTimeOverlap(
    startTime1: String,
    endTime1: String,
    startTime2: String,
    endTime2: String
): Boolean {
    val start1 = timeToMinutes(startTime1) ?: return false
    var end1 = timeToMinutes(endTime1) ?: return false

    val start2 = timeToMinutes(startTime2) ?: return false
    var end2 = timeToMinutes(endTime2) ?: return false

    // handle sleep after midnight
    if (end1 <= start1) end1 += 24 * 60
    if (end2 <= start2) end2 += 24 * 60

    return start1 < end2 && start2 < end1
}


// open date picker
private fun showSleepDatePicker(
    currentDate: String,
    context: android.content.Context,
    onDateSelected: (String) -> Unit
) {

    val calendar =
        Calendar.getInstance()


    try {

        val format =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        calendar.time =
            format.parse(currentDate)!!

    } catch (_: Exception) {
    }


    DatePickerDialog(
        context,

        { _, year, month, day ->

            val result =
                String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    day
                )

            onDateSelected(result)
        },

        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)

    ).show()
}



// open time picker
private fun showSleepTimePicker(
    context: android.content.Context,
    currentTime: String,
    onTimeSelected: (String) -> Unit
) {

    val calendar =
        Calendar.getInstance()


    try {

        val format =
            SimpleDateFormat(
                "h:mm a",
                Locale.US
            )

        calendar.time =
            format.parse(currentTime)!!

    } catch (_: Exception) {
    }


    TimePickerDialog(
        context,

        { _, hour, minute ->

            val selectedTime =
                Calendar.getInstance().apply {

                    set(
                        Calendar.HOUR_OF_DAY,
                        hour
                    )

                    set(
                        Calendar.MINUTE,
                        minute
                    )
                }


            val format =
                SimpleDateFormat(
                    "h:mm a",
                    Locale.US
                )


            onTimeSelected(
                format.format(
                    selectedTime.time
                )
            )
        },

        calendar.get(
            Calendar.HOUR_OF_DAY
        ),

        calendar.get(
            Calendar.MINUTE
        ),

        false

    ).show()
}



// display date like 12 July 2026
private fun formatSleepDate(
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



@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Add Sleep"
)
@Composable
private fun AddSleepRecordScreenPreview() {

    MaterialTheme {

        AddSleepRecordScreen(
            selectedDate = "2026-07-12",
            onBack = {},
            onSave = {}
        )
    }
}



@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Edit Sleep"
)
@Composable
private fun EditSleepRecordScreenPreview() {

    MaterialTheme {

        AddSleepRecordScreen(
            selectedDate = "2026-07-12",

            existingRecord = SleepRecord(
                id = 1,
                date = "2026-07-12",
                sleepType = "Night Sleep",
                bedtime = "11:30 PM",
                wakeUpTime = "7:00 AM",
                durationMinutes = 450,
                quality = 3,
                notes = "I felt refreshed this morning."
            ),

            onBack = {},
            onSave = {}
        )
    }
}