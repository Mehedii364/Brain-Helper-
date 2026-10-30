package com.example.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.ReminderEntity
import java.util.Calendar

object AlarmScheduler {
    private const val TAG = "AlarmScheduler"

    fun scheduleReminder(context: Context, reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = if (reminder.isAlarm) AlarmReceiver.ACTION_TRIGGER_ALARM else AlarmReceiver.ACTION_TRIGGER_REMINDER
            putExtra(AlarmReceiver.EXTRA_ID, reminder.id)
            putExtra(AlarmReceiver.EXTRA_TITLE, reminder.title)
            putExtra(AlarmReceiver.EXTRA_IS_ALARM, reminder.isAlarm)
            putExtra(AlarmReceiver.EXTRA_CHALLENGE, reminder.challengeType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = parseTimeToMillis(reminder.time)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            Log.d(TAG, "Scheduled alarm/reminder ${reminder.id} at $triggerTime")
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException while scheduling exact alarm: ${e.message}")
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } catch (ignored: Exception) {}
        }
    }

    fun cancelReminder(context: Context, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun parseTimeToMillis(timeStr: String): Long {
        val cal = Calendar.getInstance()
        try {
            val parts = timeStr.trim().split(" ")
            val timeParts = parts[0].split(":")
            var hour = timeParts[0].toInt()
            val min = timeParts.getOrNull(1)?.toInt() ?: 0
            if (parts.size > 1) {
                val amPm = parts[1].uppercase()
                if (amPm.contains("PM") && hour < 12) hour += 12
                if (amPm.contains("AM") && hour == 12) hour = 0
            }
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, min)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            if (cal.timeInMillis <= System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            return cal.timeInMillis
        } catch (e: Exception) {
            // Default 30 minutes from now if unparseable
            return System.currentTimeMillis() + 30 * 60 * 1000L
        }
    }
}
