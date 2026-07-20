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
    val totalSessions: Int,
    val last7Days: List<Float>
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    repository: SleepRepository
) : ViewModel() {

    val stats = repository.allCompletedSessions.map { sessions ->
        if (sessions.isEmpty()) return@map null
        
        val totalMinutes = sessions.sumOf { it.durationMinutes }
        
        val last7DaysData = mutableListOf<Float>()
        val today = java.time.LocalDate.now()
        for (i in 6 downTo 0) {
            val d = today.minusDays(i.toLong()).toString()
            val daySessions = sessions.filter { it.date == d }
            val minutes = daySessions.sumOf { it.durationMinutes }
            last7DaysData.add(minutes / 60f)
        }

        SleepStats(
            avgSleep = totalMinutes / sessions.size,
            longestSleep = sessions.maxOf { it.durationMinutes },
            totalHours = totalMinutes / 60,
            totalSessions = sessions.size,
            last7Days = last7DaysData
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
