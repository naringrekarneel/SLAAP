package com.slaap.sleeptracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SleepSession::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sleepSessionDao(): SleepSessionDao
}
