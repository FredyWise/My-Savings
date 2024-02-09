package com.fredy.mysavings.Data.Repository

import android.content.Context
import androidx.compose.material3.DisplayMode
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fredy.mysavings.Data.Database.Converter.LocalTimeConverter
import com.fredy.mysavings.Data.Enum.DisplayState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

interface SettingsRepository {

    // Display mode
    fun getDisplayMode(): Flow<DisplayState>
    suspend fun saveDisplayMode(displayMode: DisplayState)

    // Notifications
    fun getDailyNotification(): Flow<Boolean>
    suspend fun saveDailyNotification(enableNotification: Boolean)

    // Login
    fun getAutoLogin(): Flow<Boolean>
    suspend fun saveAutoLogin(enableAutoLogin: Boolean)

    // Authentication
    fun getBioAuth(): Flow<Boolean>
    suspend fun saveBioAuth(enableBioAuth: Boolean)
    fun getDailyNotificationTime(): Flow<LocalTime>
    suspend fun saveDailyNotificationTime(enableBioAuth: LocalTime)
}

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Settings")
        val DISPLAY_MODE = stringPreferencesKey("display_mode")
        val AUTO_LOGIN = booleanPreferencesKey("auto_login")
        val BIO_AUTH = booleanPreferencesKey("bio_auth")
        val DAILY_NOTIFICATION = booleanPreferencesKey("daily_notification")
        val DAILY_NOTIFICATION_TIME = intPreferencesKey("daily_notification_time")
    }

    override fun getDisplayMode(): Flow<DisplayState> = context.dataStore.data
        .map { preferences ->
            DisplayState.valueOf(preferences[DISPLAY_MODE] ?: "System")
        }

    override suspend fun saveDisplayMode(displayMode: DisplayState) {
        context.dataStore.edit { preferences ->
            preferences[DISPLAY_MODE] = displayMode.name
        }
    }

    override fun getDailyNotification(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DAILY_NOTIFICATION] ?: false
        }

    override suspend fun saveDailyNotification(enableNotification: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_NOTIFICATION] = enableNotification
        }
    }

    override fun getAutoLogin(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_LOGIN] ?: false
        }

    override suspend fun saveAutoLogin(enableAutoLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_LOGIN] = enableAutoLogin
        }
    }

    override fun getBioAuth(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[BIO_AUTH] ?: false
        }

    override suspend fun saveBioAuth(enableBioAuth: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIO_AUTH] = enableBioAuth
        }
    }

    override fun getDailyNotificationTime(): Flow<LocalTime> = context.dataStore.data
        .map { preferences ->
            LocalTimeConverter.intToLocalTime(preferences[DAILY_NOTIFICATION_TIME] ?: -1)
        }

    override suspend fun saveDailyNotificationTime(enableDailyNotificationTime: LocalTime) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_NOTIFICATION_TIME] = LocalTimeConverter.localTimeToInt(enableDailyNotificationTime)
        }
    }
}
