package com.example.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val PrimaryGreen = Color(0xFF0F6826)
private val LightGreenBg = Color(0xFFE8F5E9)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val DividerColor = Color(0xFFE5E5EA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiCalculatorScreen(
    profileHeight: Double = 0.0,
    profileWeight: Double = 0.0,
    onBack: () -> Unit
) {
    var height by remember(profileHeight) {
        mutableStateOf(
            if (profileHeight > 0.0) formatInputNumber(profileHeight) else ""
        )
    }
    var weight by remember(profileWeight) {
        mutableStateOf(
            if (profileWeight > 0.0) formatInputNumber(profileWeight) else ""
        )
    }
    var bmi by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "BMI Calculator",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PrimaryGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ScreenBg
                    )
                )
                HorizontalDivider(color = DividerColor)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreenBg)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Calculate,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(13.dp))
                    Column {
                        Text(
                            "Know Your BMI",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            "Profile height and weight are filled automatically when available.",
                            fontSize = 12.sp,
                            color = Color(0xFF4F6F57)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Your Measurements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = {
                            height = it
                            bmi = null
                            errorMessage = ""
                        },
                        label = { Text("Height") },
                        suffix = { Text("cm") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType=KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(Modifier.height(13.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = {
                            weight = it
                            bmi = null
                            errorMessage = ""
                        },
                        label = { Text("Weight") },
                        suffix = { Text("kg") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType=KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (errorMessage.isNotBlank()) {
                        Spacer(Modifier.height(11.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFEBEE)
                        ) {
                            Text(
                                errorMessage,
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(17.dp))

                    Button(
                        onClick = {
                            val h = height.toDoubleOrNull()
                            val w = weight.toDoubleOrNull()

                            if (h == null || w == null || h <= 0 || w <= 0) {
                                bmi = null
                                errorMessage = "Please enter a valid height and weight."
                            } else {
                                val meters = h / 100.0
                                bmi = w / (meters * meters)
                                errorMessage = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Calculate BMI", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (bmi != null) {
                Spacer(Modifier.height(18.dp))
                BmiResultCard(bmi!!)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "BMI Guide",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    BmiGuideRow("Below 18.5", "Underweight", Color(0xFF1976D2))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = DividerColor
                    )
                    BmiGuideRow("18.5 – 24.9", "Normal Weight", PrimaryGreen)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = DividerColor
                    )
                    BmiGuideRow("25.0 – 29.9", "Overweight", Color(0xFFE58A00))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = DividerColor
                    )
                    BmiGuideRow("30.0 and above", "Obesity", Color(0xFFC62828))
                }
            }

            Spacer(Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF2F2F7)
            ) {
                Row(
                    modifier = Modifier.padding(13.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "BMI is a general screening measurement and does not directly measure body fat.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun BmiResultCard(bmi: Double) {
    val category = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal Weight"
        bmi < 30.0 -> "Overweight"
        else -> "Obesity"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreenBg)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        String.format(Locale.US, "%.1f", bmi),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Your BMI", fontSize = 12.sp, color = Color(0xFF4F6F57))
                Text(
                    category,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }
    }
}

@Composable
private fun BmiGuideRow(
    range: String,
    label: String,
    dotColor: Color
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(10.dp),
            shape = CircleShape,
            color = dotColor
        ) {}
        Spacer(Modifier.width(11.dp))
        Text(
            label,
            fontWeight = FontWeight.SemiBold,
            color = TextDark,
            modifier = Modifier.weight(1f)
        )
        Text(range, fontSize = 12.sp, color = TextMuted)
    }
}

private fun formatInputNumber(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}
