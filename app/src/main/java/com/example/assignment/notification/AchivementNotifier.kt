package com.example.assignment.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.assignment.data.UserHealthData

object AchievementNotifier {

    private val notifiedBadges =
        mutableMapOf<Int, MutableSet<Int>>()

    fun checkAndNotify(
        context: Context,
        userId: Int
    ) {
        val settings =
            UserHealthData.notificationSettings(
                userId
            )

        if (!settings.achievementNotification) {
            return
        }

        val unlocked =
            UserHealthData.achievements(
                userId
            ).filter {
                it.unlocked
            }

        val alreadyNotified =
            notifiedBadges.getOrPut(
                userId
            ) {
                mutableSetOf()
            }

        unlocked.forEach { achievement ->
            if (
                achievement.id !in
                alreadyNotified
            ) {
                showNotification(
                    context,
                    userId,
                    achievement.id,
                    achievement.title
                )

                alreadyNotified.add(
                    achievement.id
                )
            }
        }
    }

    private fun showNotification(
        context: Context,
        userId: Int,
        achievementId: Int,
        title: String
    ) {
        createChannel(context)

        val allowed =
            Build.VERSION.SDK_INT < 33 ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        if (!allowed) {
            return
        }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    android.R.drawable.star_big_on
                )
                .setContentTitle(
                    "Achievement Unlocked!"
                )
                .setContentText(title)
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(
                userId * 1000 +
                        achievementId,
                notification
            )
    }

    private fun createChannel(
        context: Context
    ) {
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Achievement Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            val manager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(
                channel
            )
        }
    }

    private const val CHANNEL_ID =
        "achievement_alerts"
}
