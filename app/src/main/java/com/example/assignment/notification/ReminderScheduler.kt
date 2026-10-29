package com.example.assignment.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.assignment.data.UserNotificationSettings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ReminderScheduler {

    fun applySettings(
        context: Context,
        userId: Int,
        settings: UserNotificationSettings
    ) {
        update(
            context,
            userId,
            "water",
            settings.waterReminder,
            settings.waterTime,
            userId * 10 + 1
        )

        update(
            context,
            userId,
            "meal",
            settings.mealReminder,
            settings.mealTime,
            userId * 10 + 2
        )

        update(
            context,
            userId,
            "exercise",
            settings.exerciseReminder,
            settings.exerciseTime,
            userId * 10 + 3
        )

        update(
            context,
            userId,
            "sleep",
            settings.sleepReminder,
            settings.sleepTime,
            userId * 10 + 4
        )
    }

    private fun update(
        context: Context,
        userId: Int,
        type: String,
        enabled: Boolean,
        time: String,
        requestCode: Int
    ) {
        if (enabled) {
            schedule(
                context,
                userId,
                type,
                time,
                requestCode
            )
        } else {
            cancel(
                context,
                userId,
                type,
                requestCode
            )
        }
    }

    /**
     * Re-schedule one reminder for its next occurrence. Called by
     * ReminderReceiver after an alarm fires (the alarm is one-shot). It does not
     * touch the other three reminders, so it can't accidentally disable them.
     */
    fun rearm(
        context: Context,
        userId: Int,
        type: String,
        time: String,
        requestCode: Int
    ) {
        schedule(context, userId, type, time, requestCode)
    }

    private fun schedule(
        context: Context,
        userId: Int,
        type: String,
        time: String,
        requestCode: Int
    ) {
        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val pendingIntent =
            pendingIntent(
                context,
                userId,
                type,
                requestCode,
                time
            )

        alarmManager.cancel(pendingIntent)

        Log.d(
            "ReminderScheduler",
            "scheduled $type at " +
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(calendar.time)
        )

        // A plain (inexact) alarm: no special "exact alarm" permission needed.
        // It is a one-shot, so ReminderReceiver re-arms it for the next day
        // after it fires. Fine for a "show a reminder around this time" feature.
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun cancel(
        context: Context,
        userId: Int,
        type: String,
        requestCode: Int
    ) {
        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(
            pendingIntent(
                context,
                userId,
                type,
                requestCode
            )
        )
    }

    private fun pendingIntent(
        context: Context,
        userId: Int,
        type: String,
        requestCode: Int,
        time: String? = null
    ): PendingIntent {
        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            ).apply {
                putExtra("userId", userId)
                putExtra("type", type)
                putExtra("requestCode", requestCode)
                if (time != null) putExtra("time", time)
            }

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }
}
