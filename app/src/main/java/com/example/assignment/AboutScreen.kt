package com.example.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "About",
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreenBg)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(13.dp))

                    Text(
                        "Health Coach",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        "Version 1.0",
                        fontSize = 12.sp,
                        color = Color(0xFF4F6F57)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "A simple health companion for tracking your daily habits and progress.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF4F6F57)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "What You Can Do",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    AboutFeatureRow(
                        Icons.Default.WaterDrop,
                        Color(0xFFEAF3FF),
                        Color(0xFF1976D2),
                        "Track Daily Habits",
                        "Water, meals, exercise and sleep"
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 66.dp),
                        color = DividerColor
                    )
                    AboutFeatureRow(
                        Icons.Default.Flag,
                        LightGreenBg,
                        PrimaryGreen,
                        "Set Health Goals",
                        "Create targets that suit your routine"
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 66.dp),
                        color = DividerColor
                    )
                    AboutFeatureRow(
                        Icons.Default.EmojiEvents,
                        Color(0xFFFFF4E5),
                        Color(0xFFE58A00),
                        "Earn Achievements",
                        "Unlock badges from real progress"
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 66.dp),
                        color = DividerColor
                    )
                    AboutFeatureRow(
                        Icons.Default.Lightbulb,
                        Color(0xFFF1E9FF),
                        Color(0xFF5E35B1),
                        "View Weekly Insight",
                        "Understand your weekly health patterns"
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF2F2F7)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(Modifier.width(9.dp))
                    Text(
                        "Health Coach is designed for general wellness tracking and educational purposes.",
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "© 2026 Health Coach",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun AboutFeatureRow(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
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
        Column {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Text(
                subtitle,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}
