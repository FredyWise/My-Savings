package com.fredy.preferences.di

import android.content.Context
import com.fredy.preferences.data.PreferencesRepositoryImpl
import com.fredy.preferences.domain.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext appContext: Context
    ): PreferencesRepository = PreferencesRepositoryImpl(
        appContext
    )
}