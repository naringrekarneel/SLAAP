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
    val avgSleepMinutes: Long,
    val longestSleepMinutes: Long,
    val totalSessions: Int,
    val distributionBuckets: List<Int>, // 4, 5, 6, 7, 8, 9, 10+
    val mostCommonString: String,
    val goalAdherencePercent: Int,
    val consistencyString: String
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    repository: SleepRepository
) : ViewModel() {

    val stats = repository.allCompletedSessions.map { sessions ->
        if (sessions.isEmpty()) return@map null
        
        val totalMinutes = sessions.sumOf { it.durationMinutes }
        val avgMinutes = totalMinutes / sessions.size

        // Buckets for 4, 5, 6, 7, 8, 9, 10+ hours
        val buckets = IntArray(7) { 0 }
        sessions.forEach { session ->
            val hours = (session.durationMinutes / 60).toInt()
            when {
                hours <= 4 -> buckets[0]++
                hours == 5 -> buckets[1]++
                hours == 6 -> buckets[2]++
                hours == 7 -> buckets[3]++
                hours == 8 -> buckets[4]++
                hours == 9 -> buckets[5]++
                hours >= 10 -> buckets[6]++
            }
        }

        val maxBucketIndex = buckets.indexOfFirst { it == buckets.maxOrNull() }
        val maxBucketLabel = when (maxBucketIndex) {
            0 -> "<5"
            6 -> "10+"
            else -> "${maxBucketIndex + 4}-${maxBucketIndex + 5}"
        }
        val mostCommonString = "$maxBucketLabel Hours (${buckets.maxOrNull()} nights)"

        val goalMetCount = sessions.count { it.durationMinutes >= 7 * 60 } // Assume 7 hrs is goal
        val goalPercent = if (sessions.isNotEmpty()) (goalMetCount * 100) / sessions.size else 0

        SleepStats(
            avgSleepMinutes = avgMinutes,
            longestSleepMinutes = sessions.maxOf { it.durationMinutes },
            totalSessions = sessions.size,
            distributionBuckets = buckets.toList(),
            mostCommonString = mostCommonString,
            goalAdherencePercent = goalPercent,
            consistencyString = "6.5-7.5 hrs" // Placeholder for now
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
