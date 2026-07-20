package com.slaap.sleeptracker.di

import android.content.Context
import androidx.room.Room
import com.slaap.sleeptracker.data.AppDatabase
import com.slaap.sleeptracker.data.SleepSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "slaap_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSleepSessionDao(appDatabase: AppDatabase): SleepSessionDao {
        return appDatabase.sleepSessionDao()
    }
}
