package com.example.assignment.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.assignment.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The system clears all AlarmManager alarms on reboot. This receiver runs once
 * after the device boots and re-schedules every account's reminders from the
 * settings stored in Room, so the reminders survive a restart.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.d("BootReceiver", "device booted, re-scheduling reminders")

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                for (user in db.userDao().getAllUsers()) {
                    val settings = db.notificationSettingsDao()
                        .getSettingsForUser(user.id)?.toModel() ?: continue
                    ReminderScheduler.applySettings(context, user.id, settings)
                }
            } catch (e: Exception) {
                Log.w("BootReceiver", "reschedule failed: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
