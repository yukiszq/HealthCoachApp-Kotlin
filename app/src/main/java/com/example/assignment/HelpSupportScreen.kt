package com.example.assignment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryGreen = Color(0xFF0F6826)
private val LightGreenBg = Color(0xFFE8F5E9)
private val ScreenBg = Color(0xFFF9F9FB)
private val TextDark = Color(0xFF1C1C1E)
private val TextMuted = Color(0xFF8E8E93)
private val DividerColor = Color(0xFFE5E5EA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Help & Support",
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
                                Icons.Default.Help,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(13.dp))

                    Column {
                        Text(
                            "How can we help?",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            "Find quick answers about using Health Coach.",
                            fontSize = 12.sp,
                            color = Color(0xFF4F6F57)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Frequently Asked Questions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(Modifier.height(10.dp))

            HelpQuestion(
                icon = Icons.Default.AddCircle,
                iconBg = Color(0xFFEAF3FF),
                iconColor = Color(0xFF1976D2),
                question = "How do I add a health record?",
                answer = "Open Trackers, choose Water, Meal, Exercise or Sleep, then tap the + button to add a record."
            )

            Spacer(Modifier.height(10.dp))

            HelpQuestion(
                icon = Icons.Default.Flag,
                iconBg = LightGreenBg,
                iconColor = PrimaryGreen,
                question = "How do I change my health goals?",
                answer = "Open the side menu and choose Health Goals. Enter your new targets and tap Save Goals."
            )

            Spacer(Modifier.height(10.dp))

            HelpQuestion(
                icon = Icons.Default.EmojiEvents,
                iconBg = Color(0xFFFFF4E5),
                iconColor = Color(0xFFE58A00),
                question = "How do achievements work?",
                answer = "Badge progress updates automatically from your real tracker records and completed targets."
            )

            Spacer(Modifier.height(10.dp))

            HelpQuestion(
                icon = Icons.Default.Lightbulb,
                iconBg = Color(0xFFF1E9FF),
                iconColor = Color(0xFF5E35B1),
                question = "Why is my Insight page empty?",
                answer = "A new user starts with no health records. Keep logging your activities and weekly Insight will update automatically."
            )

            Spacer(Modifier.height(10.dp))

            HelpQuestion(
                icon = Icons.Default.Notifications,
                iconBg = Color(0xFFFFF0E6),
                iconColor = Color(0xFFD84315),
                question = "How do reminders work?",
                answer = "Open Notifications, enable the reminders you want, choose the time and save your reminder settings."
            )

            Spacer(Modifier.height(10.dp))

            HelpQuestion(
                icon = Icons.Default.Calculate,
                iconBg = LightGreenBg,
                iconColor = PrimaryGreen,
                question = "How does the BMI Calculator work?",
                answer = "It uses height and weight to calculate BMI. Your profile values are filled automatically when available."
            )

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(17.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(13.dp),
                        color = Color(0xFFF2F2F7)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = PrimaryGreen
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            "Still Need Help?",
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            "Check your records and settings, then try the feature again.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun HelpQuestion(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(13.dp),
                    color = iconBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    if (expanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = null,
                    tint = TextMuted
                )
            }

            if (expanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 69.dp),
                    color = DividerColor
                )

                Text(
                    answer,
                    modifier = Modifier.padding(
                        start = 69.dp,
                        end = 15.dp,
                        top = 11.dp,
                        bottom = 15.dp
                    ),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextMuted
                )
            }
        }
    }
}
