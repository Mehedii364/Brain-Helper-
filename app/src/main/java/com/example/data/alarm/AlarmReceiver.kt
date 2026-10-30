package com.example.data.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.BrainHelperApp
import com.example.MainActivity
import com.example.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_ID, 0)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: context.getString(R.string.notification_title)
        val isAlarm = intent.getBooleanExtra(EXTRA_IS_ALARM, true)
        val challenge = intent.getStringExtra(EXTRA_CHALLENGE) ?: "GENTLE"

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, BrainHelperApp.CHANNEL_REMINDERS_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(if (isAlarm) "⏰ $title" else "🔔 $title")
            .setContentText(
                if (isAlarm) {
                    "Brain Helper Smart Alarm ($challenge Wake Mode) - Tap to review your day."
                } else {
                    "Brain Helper Scheduled Reminder: Make today count."
                }
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(if (isAlarm) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(reminderId, notification)
    }

    companion object {
        const val ACTION_TRIGGER_ALARM = "com.example.ACTION_TRIGGER_ALARM"
        const val ACTION_TRIGGER_REMINDER = "com.example.ACTION_TRIGGER_REMINDER"
        const val EXTRA_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_reminder_title"
        const val EXTRA_IS_ALARM = "extra_is_alarm"
        const val EXTRA_CHALLENGE = "extra_challenge"
    }
}
