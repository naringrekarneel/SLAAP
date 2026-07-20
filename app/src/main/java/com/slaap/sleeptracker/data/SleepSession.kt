package com.slaap.sleeptracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "sleep_sessions")
data class SleepSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: String, // Stored as ISO string
    val endTime: String?, // Null if currently active
    val durationMinutes: Long = 0,
    val date: String // Represents the logical day of the sleep (e.g., date of wakeup)
)
