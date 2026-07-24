package com.slaap.sleeptracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaap.sleeptracker.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.slaap.sleeptracker.alarm.AlarmItem
import com.slaap.sleeptracker.alarm.AlarmScheduler
import java.time.LocalDateTime

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SleepRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val activeSession = repository.activeSessionFlow
    val lastSession = repository.lastCompletedSession

    fun toggleSleep() {
        viewModelScope.launch {
            repository.toggleSleepSession()
        }
    }

    fun startSleepIfNotActive() {
        viewModelScope.launch {
            if (repository.getActiveSession() == null) {
                repository.toggleSleepSession()
            }
        }
    }

    fun scheduleAlarm(hour: Int, minute: Int) {
        val now = LocalDateTime.now()
        var alarmTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (alarmTime.isBefore(now)) {
            alarmTime = alarmTime.plusDays(1)
        }
        alarmScheduler.schedule(AlarmItem(alarmTime, "Wake up!"))
    }
}
