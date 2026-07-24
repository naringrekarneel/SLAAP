package com.slaap.sleeptracker.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaap.sleeptracker.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.ZoneId
import java.time.YearMonth
import javax.inject.Inject

data class SleepStats(
    val avgSleepMinutes: Long,
    val longestSleepMinutes: Long,
    val totalSessions: Int,
    val dailyHours: List<Float>, // Hours for each day in the month
    val mostCommonString: String,
    val goalAdherencePercent: Int,
    val consistencyString: String,
    val currentMonthLabel: String
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    repository: SleepRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())

    val stats = combine(repository.allCompletedSessions, _selectedMonth) { sessions, selectedMonth ->
        // Filter sessions to the selected month
        val sessionsInMonth = sessions.filter { 
            val date = java.time.LocalDate.parse(it.date)
            YearMonth.from(date) == selectedMonth
        }

        val daysInMonth = selectedMonth.lengthOfMonth()
        val dailyHours = FloatArray(daysInMonth) { 0f }
        
        sessionsInMonth.forEach { session ->
            val date = java.time.LocalDate.parse(session.date)
            val dayIndex = date.dayOfMonth - 1
            dailyHours[dayIndex] += (session.durationMinutes / 60f)
        }

        val monthLabel = "${selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${selectedMonth.year}"

        val totalMinutes = sessionsInMonth.sumOf { it.durationMinutes }
        val avgMinutes = if (sessionsInMonth.isNotEmpty()) totalMinutes / sessionsInMonth.size else 0L
        val longestSleep = if (sessionsInMonth.isNotEmpty()) sessionsInMonth.maxOf { it.durationMinutes } else 0L

        // most common string: can use max sleep for this month as a replacement
        val maxSleep = dailyHours.maxOrNull()?.takeIf { it > 0f }
        val mostCommonString = if (maxSleep != null) String.format("%.1f Hours (Max)", maxSleep) else "N/A"

        val goalMetCount = sessionsInMonth.count { it.durationMinutes >= 7 * 60 } // Assume 7 hrs is goal
        val goalPercent = if (sessionsInMonth.isNotEmpty()) (goalMetCount * 100) / sessionsInMonth.size else 0

        SleepStats(
            avgSleepMinutes = avgMinutes,
            longestSleepMinutes = longestSleep,
            totalSessions = sessionsInMonth.size,
            dailyHours = dailyHours.toList(),
            mostCommonString = mostCommonString,
            goalAdherencePercent = goalPercent,
            consistencyString = "6.5-7.5 hrs", // Placeholder
            currentMonthLabel = monthLabel
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun nextMonth() {
        _selectedMonth.update { it.plusMonths(1) }
    }

    fun previousMonth() {
        _selectedMonth.update { it.minusMonths(1) }
    }
}
