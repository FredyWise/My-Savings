package com.fredy.preferences.domain

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface PreferencesRepository {

    // Display mode
    fun getDisplayMode(): Flow<DisplayMode>
    suspend fun saveDisplayMode(displayMode: DisplayMode)
    fun getThemeColor(): Flow<Color?>
    suspend fun saveThemeColor(color: Color?)
    fun getIncomeColor(displayMode: DisplayMode): Flow<Color>
    suspend fun saveIncomeColor(color: Color?)
    fun getExpenseColor(displayMode: DisplayMode): Flow<Color>
    suspend fun saveExpenseColor(color: Color?)

    fun getTransferColor(displayMode: DisplayMode): Flow<Color>
    suspend fun saveTransferColor(color: Color?)

    // Notifications
    fun getDailyNotification(): Flow<Boolean>
    suspend fun saveDailyNotification(enableNotification: Boolean)
    fun getDailyNotificationTime(): Flow<LocalTime>
    suspend fun saveDailyNotificationTime(dailyNotificationTime: LocalTime)

    // Login
    fun getAutoLogin(): Flow<Boolean>
    suspend fun saveAutoLogin(enableAutoLogin: Boolean)

    // Authentication
    fun getBioAuth(): Flow<Boolean>
    suspend fun saveBioAuth(enableBioAuth: Boolean)
    fun bioAuthStatus(): Boolean

    // View
    fun getCarryOn(): Flow<Boolean>
    suspend fun saveCarryOn(enableCarryOn: Boolean)
    fun getShowTotal(): Flow<Boolean>
    suspend fun saveShowTotal(enableShowTotal: Boolean)

    // ALL
    fun getAllPreferenceSettings(): Flow<PreferenceSettings>
//    fun getAllPreferenceView(): Flow<FilterState>
}


