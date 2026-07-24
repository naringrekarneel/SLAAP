package com.slaap.sleeptracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepSessionDao {
    @Insert
    suspend fun insertSession(session: SleepSession)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SleepSession>)

    @Update
    suspend fun updateSession(session: SleepSession)

    @Query("SELECT * FROM sleep_sessions WHERE endTime IS NULL LIMIT 1")
    suspend fun getActiveSession(): SleepSession?

    @Query("SELECT * FROM sleep_sessions WHERE endTime IS NULL LIMIT 1")
    fun getActiveSessionFlow(): Flow<SleepSession?>

    @Query("SELECT * FROM sleep_sessions WHERE endTime IS NOT NULL ORDER BY id DESC")
    fun getAllCompletedSessions(): Flow<List<SleepSession>>

    @Query("SELECT * FROM sleep_sessions WHERE endTime IS NOT NULL ORDER BY id DESC LIMIT 1")
    fun getLastCompletedSession(): Flow<SleepSession?>

    @Query("DELETE FROM sleep_sessions")
    suspend fun deleteAll()

    @Query("SELECT * FROM sleep_sessions ORDER BY id ASC")
    suspend fun getAllSessionsSync(): List<SleepSession>
}
