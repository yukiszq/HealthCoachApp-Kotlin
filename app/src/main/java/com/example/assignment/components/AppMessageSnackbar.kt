package com.example.assignment.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val PrimaryGreen = Color(0xFF0F6826)
private val LightMessageBg = Color(0xFFF1F3F2)
private val TextDark = Color(0xFF1C1C1E)
private val DeleteRed = Color(0xFFD32F2F)


@Composable
fun AppMessageSnackbar(
    message: String,
    isDelete: Boolean = false
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            ),

        shape = RoundedCornerShape(28.dp),

        color = LightMessageBg,

        shadowElevation = 4.dp
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 14.dp
            ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,

                color = if (isDelete) {
                    DeleteRed
                } else {
                    PrimaryGreen
                }
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = if (isDelete) {
                            Icons.Default.Delete
                        } else {
                            Icons.Default.Check
                        },

                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }


            Spacer(
                modifier = Modifier.width(16.dp)
            )


            Text(
                text = message,
                fontSize = 16.sp,
                color = TextDark
            )
        }
    }
}