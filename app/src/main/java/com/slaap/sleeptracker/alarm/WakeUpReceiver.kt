package com.slaap.sleeptracker.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.slaap.sleeptracker.repository.SleepRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.slaap.sleeptracker.alarm.AlarmItem
import com.slaap.sleeptracker.alarm.AlarmScheduler
import java.time.LocalDateTime

@AndroidEntryPoint
class WakeUpReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: SleepRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        // Dismiss the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(101)

        // Stop the 5-minute recurring alarm loop
        alarmScheduler.cancel(AlarmItem(LocalDateTime.now(), ""))

        // Launch coroutine to update the database
        CoroutineScope(Dispatchers.IO).launch {
            repository.toggleSleepSession()
        }
    }
}
