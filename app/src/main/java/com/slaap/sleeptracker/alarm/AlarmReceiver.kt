package com.slaap.sleeptracker.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.slaap.sleeptracker.R
import com.slaap.sleeptracker.alarm.AlarmItem
import com.slaap.sleeptracker.alarm.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Used for the sleep tracker alarm"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent for the "Wake Up" action button
        val wakeUpIntent = Intent(context, WakeUpReceiver::class.java)
        val wakeUpPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            wakeUpIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle("Alarm: Are you awake?")
            .setContentText("Tap 'Wake Up' to log your sleep and turn off the alarm.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false) // Require them to tap the button
            .setOngoing(true) // Make it harder to swipe away
            // Add actions
            .addAction(android.R.drawable.ic_lock_idle_alarm, "Wake Up", wakeUpPendingIntent)
            .build()

        notificationManager.notify(101, notification)

        // Automatically schedule the next alarm in 5 minutes until they wake up
        val newTime = LocalDateTime.now().plusMinutes(5)
        alarmScheduler.schedule(AlarmItem(newTime, "Wake up loop"))
    }
}
