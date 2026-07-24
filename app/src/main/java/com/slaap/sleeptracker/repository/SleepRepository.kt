package com.slaap.sleeptracker.repository

import com.slaap.sleeptracker.data.SleepSession
import com.slaap.sleeptracker.data.SleepSessionDao
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepRepository @Inject constructor(
    private val sleepSessionDao: SleepSessionDao
) {
    val activeSessionFlow: Flow<SleepSession?> = sleepSessionDao.getActiveSessionFlow()
    val allCompletedSessions: Flow<List<SleepSession>> = sleepSessionDao.getAllCompletedSessions()
    val lastCompletedSession: Flow<SleepSession?> = sleepSessionDao.getLastCompletedSession()

    suspend fun toggleSleepSession() {
        val activeSession = sleepSessionDao.getActiveSession()
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        if (activeSession == null) {
            // Start a new session
            val newSession = SleepSession(
                startTime = now.format(formatter),
                endTime = null,
                date = now.toLocalDate().toString()
            )
            sleepSessionDao.insertSession(newSession)
        } else {
            // End current session
            val start = LocalDateTime.parse(activeSession.startTime, formatter)
            val durationMinutes = Duration.between(start, now).toMinutes()
            
            // Re-evaluate date: usually sleep date is associated with the morning you wake up.
            // We'll use the wakeup date as the session's logical date.
            
            val updatedSession = activeSession.copy(
                endTime = now.format(formatter),
                durationMinutes = durationMinutes,
                date = now.toLocalDate().toString()
            )
            sleepSessionDao.updateSession(updatedSession)
        }
    }
    
    suspend fun getActiveSession(): SleepSession? = sleepSessionDao.getActiveSession()

    suspend fun deleteAllData() {
        sleepSessionDao.deleteAll()
    }

    suspend fun insertSessions(sessions: List<SleepSession>) {
        sleepSessionDao.insertSessions(sessions)
    }

    suspend fun getAllSessionsSync(): List<SleepSession> {
        return sleepSessionDao.getAllSessionsSync()
    }
}
