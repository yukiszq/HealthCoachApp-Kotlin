package com.example.assignment.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.assignment.data.UserHealthData
import com.example.assignment.util.getCurrentDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val userId =
            intent.getIntExtra("userId", 0)

        val type =
            intent.getStringExtra("type")
                ?: return

        val time = intent.getStringExtra("time")
        val requestCode = intent.getIntExtra("requestCode", -1)

        Log.d("ReminderReceiver", "alarm fired: type=$type userId=$userId")

        // Re-arm THIS reminder for the next day straight away (the alarm is a
        // one-shot). Done here, before the coroutine, and only for this one
        // reminder, so a later failure can't disable the others.
        if (time != null && requestCode != -1) {
            try {
                ReminderScheduler.rearm(context, userId, type, time, requestCode)
            } catch (e: Exception) {
                Log.w("ReminderReceiver", "rearm failed: ${e.message}")
            }
        }

        // The alarm can fire in a fresh process where the in-memory cache is
        // empty, so load today's data from Room first, then post the reminder.
        // Each step is guarded so a failure in one never eats the notification.
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                try {
                    UserHealthData.initialize(context)
                    UserHealthData.loadFromRoom(userId)
                } catch (e: Exception) {
                    Log.w("ReminderReceiver", "loadFromRoom failed: ${e.message}")
                }
                try {
                    notify(context, userId, type)
                } catch (e: Exception) {
                    Log.w("ReminderReceiver", "notify failed: ${e.message}")
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun notify(
        context: Context,
        userId: Int,
        type: String
    ) {
        createChannel(context)

        val goals =
            UserHealthData.goals(userId)

        val today =
            getCurrentDate()

        val title: String
        val message: String
        val notificationId: Int

        when (type) {
            "water" -> {
                val current =
                    UserHealthData.waterTotal(
                        userId,
                        today
                    )

                val left =
                    (goals.waterGoal - current)
                        .coerceAtLeast(0)

                title = "Hydration Reminder"
                notificationId = userId * 100 + 1

                message =
                    if (left > 0) {
                        "You've had $current / ${goals.waterGoal} ml today. $left ml left."
                    } else {
                        "Great job! You've reached your water goal today."
                    }
            }

            "meal" -> {
                val current =
                    UserHealthData.mealTotal(
                        userId,
                        today
                    )

                val left =
                    (goals.mealGoal - current)
                        .coerceAtLeast(0)

                title = "Meal Reminder"
                notificationId = userId * 100 + 2

                message =
                    if (left > 0) {
                        "You've logged $current / ${goals.mealGoal} kcal today. $left kcal remaining."
                    } else {
                        "Your meal goal for today has been reached."
                    }
            }

            "exercise" -> {
                val current =
                    UserHealthData.exerciseMinutes(
                        userId,
                        today
                    ).roundToInt()

                val left =
                    (goals.exerciseGoal - current)
                        .coerceAtLeast(0)

                title = "Exercise Reminder"
                notificationId = userId * 100 + 3

                message =
                    if (left > 0) {
                        "You've exercised $current / ${goals.exerciseGoal} mins today. $left mins left."
                    } else {
                        "Great work! You've reached your exercise goal today."
                    }
            }

            else -> {
                val current =
                    UserHealthData.sleepMinutes(
                        userId,
                        today
                    )

                title = "Sleep Reminder"
                notificationId = userId * 100 + 4

                message =
                    if (current < goals.sleepGoal) {
                        "You've logged ${formatMinutes(current)} / ${formatMinutes(goals.sleepGoal)} today."
                    } else {
                        "Your sleep goal for today has been reached."
                    }
            }
        }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build()

        val allowed =
            Build.VERSION.SDK_INT < 33 ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        if (allowed) {
            NotificationManagerCompat
                .from(context)
                .notify(
                    notificationId,
                    notification
                )
            Log.d("ReminderReceiver", "notification posted: $title")
        } else {
            Log.w("ReminderReceiver", "POST_NOTIFICATIONS not granted, nothing shown")
        }
        // (re-arming for tomorrow already happened in onReceive)
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
                    "Health Reminders",
                    // HIGH = pops up as a heads-up, with the default sound.
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description =
                        "Daily health reminder notifications"
                }

            val manager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(
                channel
            )
        }
    }

    private fun formatMinutes(
        minutes: Int
    ): String {
        val hours = minutes / 60
        val remaining = minutes % 60

        return when {
            hours > 0 && remaining > 0 ->
                "${hours}h ${remaining}m"

            hours > 0 ->
                "${hours}h"

            else ->
                "${remaining}m"
        }
    }

    companion object {
        // v4: fresh id so the new importance/sound takes effect (a channel's
        // settings cannot be changed in code once it already exists).
        const val CHANNEL_ID = "health_reminders_v4"
    }
}
