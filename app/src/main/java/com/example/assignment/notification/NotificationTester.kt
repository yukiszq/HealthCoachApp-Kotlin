package com.example.assignment.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Posts a notification immediately (no alarm involved).
 *
 * Used by the "Send a test notification" button on the Notifications screen to
 * check the notification pipeline (channel + permission + display) without
 * waiting for a scheduled reminder. This one is made deliberately loud
 * (heads-up) so it cannot be missed; the real daily reminders stay silent.
 */
object NotificationTester {

    private const val TEST_CHANNEL_ID = "health_test"

    fun sendTest(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TEST_CHANNEL_ID,
                "Test Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Used by the test button on the Notifications screen" }
            nm.createNotificationChannel(channel)
        }

        val runtimeOk =
            Build.VERSION.SDK_INT < 33 ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        val nmc = NotificationManagerCompat.from(context)

        if (!runtimeOk || !nmc.areNotificationsEnabled()) {
            Toast.makeText(
                context,
                "Notifications are OFF for this app. Turn them on in the screen that just opened.",
                Toast.LENGTH_LONG
            ).show()
            runCatching {
                context.startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            return
        }

        val text = "Notifications are working. Your daily reminders will appear like this."
        val notification = NotificationCompat.Builder(context, TEST_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Test Reminder")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        nmc.notify(9999, notification)
        Log.d("NotificationTester", "test notification posted")
        Toast.makeText(
            context,
            "Test notification sent. Swipe down from the top if you don't see it.",
            Toast.LENGTH_LONG
        ).show()
    }
}
