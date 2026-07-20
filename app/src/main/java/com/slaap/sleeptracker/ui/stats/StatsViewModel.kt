package com.slaap.sleeptracker.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaap.sleeptracker.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SleepStats(
    val avgSleep: Long,
    val longestSleep: Long,
    val totalHours: Long,
    val totalSessions: Int
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    repository: SleepRepository
) : ViewModel() {

    val stats = repository.allCompletedSessions.map { sessions ->
        if (sessions.isEmpty()) return@map null
        
        val totalMinutes = sessions.sumOf { it.durationMinutes }
        SleepStats(
            avgSleep = totalMinutes / sessions.size,
            longestSleep = sessions.maxOf { it.durationMinutes },
            totalHours = totalMinutes / 60,
            totalSessions = sessions.size
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
