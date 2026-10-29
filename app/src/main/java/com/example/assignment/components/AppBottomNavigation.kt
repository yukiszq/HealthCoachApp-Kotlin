package com.example.assignment.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

private val PrimaryGreen =
    Color(0xFF0F6826)

private val LightGreenBg =
    Color(0xFFE8F5E9)

private val TextMuted =
    Color(0xFF8E8E93)

@Composable
fun AppBottomNavigation(
    selectedItem: String,
    onHomeClick: () -> Unit = {},
    onTrackersClick: () -> Unit = {},
    onInsightClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor =
            Color.White
    ) {
        NavigationBarItem(
            selected =
                selectedItem == "Home",
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription =
                        "Home"
                )
            },
            label = {
                Text(
                    "Home",
                    fontSize = 11.sp
                )
            },
            colors =
                NavigationBarItemDefaults
                    .colors(
                        selectedIconColor =
                            PrimaryGreen,
                        selectedTextColor =
                            PrimaryGreen,
                        unselectedIconColor =
                            TextMuted,
                        unselectedTextColor =
                            TextMuted,
                        indicatorColor =
                            LightGreenBg
                    )
        )

        NavigationBarItem(
            selected =
                selectedItem ==
                        "Trackers",
            onClick =
                onTrackersClick,
            icon = {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription =
                        "Trackers"
                )
            },
            label = {
                Text(
                    "Trackers",
                    fontSize = 11.sp
                )
            },
            colors =
                NavigationBarItemDefaults
                    .colors(
                        selectedIconColor =
                            PrimaryGreen,
                        selectedTextColor =
                            PrimaryGreen,
                        unselectedIconColor =
                            TextMuted,
                        unselectedTextColor =
                            TextMuted,
                        indicatorColor =
                            LightGreenBg
                    )
        )

        NavigationBarItem(
            selected =
                selectedItem ==
                        "Insight",
            onClick =
                onInsightClick,
            icon = {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription =
                        "Insight"
                )
            },
            label = {
                Text(
                    "Insight",
                    fontSize = 11.sp
                )
            },
            colors =
                NavigationBarItemDefaults
                    .colors(
                        selectedIconColor =
                            PrimaryGreen,
                        selectedTextColor =
                            PrimaryGreen,
                        unselectedIconColor =
                            TextMuted,
                        unselectedTextColor =
                            TextMuted,
                        indicatorColor =
                            LightGreenBg
                    )
        )
    }
}
