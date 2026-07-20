package com.slaap.sleeptracker.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

object TimeUtils {
    fun formatTime(isoString: String): String {
        val dt = LocalDateTime.parse(isoString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return dt.format(formatter)
    }

    fun formatDate(isoString: String): String {
        val dt = LocalDateTime.parse(isoString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        return dt.format(formatter)
    }

    fun formatDuration(minutes: Long): String {
        val hours = minutes / 60
        val mins = minutes % 60
        if (hours == 0L) return "${mins}m"
        return "${hours}h ${mins}m"
    }
}
