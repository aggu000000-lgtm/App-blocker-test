package com.sharma.focusblocker.di

import android.app.Application
import android.content.Context
import com.sharma.focusblocker.data.AppDatabase
import com.sharma.focusblocker.data.BlockerPreferences
import com.sharma.focusblocker.data.ScheduleDao
import com.sharma.focusblocker.data.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideScheduleDao(appDatabase: AppDatabase): ScheduleDao {
        return appDatabase.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideBlockerPreferences(@ApplicationContext context: Context): BlockerPreferences {
        return BlockerPreferences(context)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(
        scheduleDao: ScheduleDao,
        blockerPreferences: BlockerPreferences
    ): ScheduleRepository {
        return ScheduleRepository(scheduleDao, blockerPreferences)
    }
}
