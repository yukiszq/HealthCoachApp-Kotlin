package com.example.assignment.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val PrimaryGreen =
    Color(0xFF0F6826)

private val LightGreenBg =
    Color(0xFFE8F5E9)

private val TextDark =
    Color(0xFF1C1C1E)

private val TextMuted =
    Color(0xFF8E8E93)

private val DividerColor =
    Color(0xFFE5E5EA)

private val DangerRed =
    Color(0xFFC62828)

private val DangerBg =
    Color(0xFFFFEBEE)

@Composable
fun AppNavigationDrawer(
    userName: String,
    profileImageUri: String = "",
    todayProgress: Int = 0,
    onProfileClick: () -> Unit = {},
    onAchievementClick: () -> Unit = {},
    onHealthGoalsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onBmiClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .padding(horizontal = 16.dp)
    ) {

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightGreenBg
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                if (
                    profileImageUri.isNotBlank()
                ) {

                    AsyncImage(
                        model = profileImageUri,
                        contentDescription =
                            "Profile photo",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale =
                            ContentScale.Crop
                    )

                } else {

                    Surface(
                        modifier =
                            Modifier.size(56.dp),
                        shape = CircleShape,
                        color = Color.White
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Person,
                                contentDescription =
                                    "Profile",
                                tint = PrimaryGreen,
                                modifier =
                                    Modifier.size(27.dp)
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            userName.ifBlank {
                                "User"
                            },
                        fontSize = 18.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            if (
                                todayProgress > 0
                            ) {
                                "$todayProgress% of today's goals completed"
                            } else {
                                "Start tracking your health today"
                            },
                        fontSize = 12.sp,
                        color =
                            Color(0xFF55705C)
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        DrawerSectionTitle(
            title = "PERSONAL"
        )

        DrawerRow(
            icon =
                Icons.Default.Person,
            label = "Profile",
            iconBackground =
                LightGreenBg,
            iconColor =
                PrimaryGreen,
            onClick =
                onProfileClick
        )

        DrawerRow(
            icon =
                Icons.Default
                    .EmojiEvents,
            label = "Achievements",
            iconBackground =
                Color(0xFFFFF4E5),
            iconColor =
                Color(0xFFE58A00),
            onClick =
                onAchievementClick
        )

        DrawerRow(
            icon =
                Icons.Default.Flag,
            label = "Health Goals",
            iconBackground =
                Color(0xFFEAF3FF),
            iconColor =
                Color(0xFF1976D2),
            onClick =
                onHealthGoalsClick
        )

        HorizontalDivider(
            modifier =
                Modifier.padding(
                    vertical = 10.dp
                ),
            thickness = 1.dp,
            color = DividerColor
        )

        DrawerSectionTitle(
            title = "SETTINGS"
        )

        DrawerRow(
            icon =
                Icons.Default
                    .Notifications,
            label = "Notifications",
            iconBackground =
                Color(0xFFFFF0E6),
            iconColor =
                Color(0xFFD84315),
            onClick =
                onNotificationClick
        )

        DrawerRow(
            icon =
                Icons.Default.Calculate,
            label = "BMI Calculator",
            iconBackground =
                LightGreenBg,
            iconColor =
                PrimaryGreen,
            onClick =
                onBmiClick
        )

        DrawerRow(
            icon =
                Icons.Default.Help,
            label = "Help & Support",
            iconBackground =
                Color(0xFFF1E9FF),
            iconColor =
                Color(0xFF5E35B1),
            onClick =
                onHelpClick
        )

        DrawerRow(
            icon =
                Icons.Default.Info,
            label = "About",
            iconBackground =
                Color(0xFFF2F2F7),
            iconColor =
                Color(0xFF5C5C5C),
            onClick =
                onAboutClick
        )

        Spacer(
            modifier =
                Modifier.weight(1f)
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = DividerColor
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSignOutClick()
                },
            shape =
                RoundedCornerShape(16.dp),
            color = DangerBg
        ) {

            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 13.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(38.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Logout,
                            contentDescription =
                                "Sign Out",
                            tint = DangerRed,
                            modifier =
                                Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )

                Text(
                    text = "Sign Out",
                    fontWeight =
                        FontWeight.SemiBold,
                    color = DangerRed,
                    modifier =
                        Modifier.weight(1f)
                )

                Icon(
                    imageVector =
                        Icons.Default
                            .ChevronRight,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier =
                        Modifier.size(20.dp)
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )
    }
}

@Composable
private fun DrawerSectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 10.sp,
        fontWeight =
            FontWeight.Bold,
        color = TextMuted,
        modifier =
            Modifier.padding(
                start = 10.dp,
                bottom = 5.dp
            )
    )
}

@Composable
private fun DrawerRow(
    icon: ImageVector,
    label: String,
    iconBackground: Color,
    iconColor: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    16.dp
                )
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 10.dp,
                vertical = 9.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier =
                Modifier.size(40.dp),
            shape =
                RoundedCornerShape(13.dp),
            color = iconBackground
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription =
                        label,
                    tint = iconColor,
                    modifier =
                        Modifier.size(21.dp)
                )
            }
        }

        Spacer(
            modifier =
                Modifier.width(13.dp)
        )

        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight =
                FontWeight.Medium,
            color = TextDark,
            modifier =
                Modifier.weight(1f)
        )

        Icon(
            imageVector =
                Icons.Default.ChevronRight,
            contentDescription = null,
            tint =
                Color(0xFFB0B0B5),
            modifier =
                Modifier.size(19.dp)
        )
    }
}