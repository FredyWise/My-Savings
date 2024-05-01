package com.fredy.mysavings.Feature.Domain.Repository

import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Feature.Data.Enum.DisplayState
import com.fredy.mysavings.Feature.Presentation.ViewModels.PreferencesViewModel.PreferencesState
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.FilterState
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface PreferencesRepository {

    // Display mode
    fun getDisplayMode(): Flow<DisplayState>
    suspend fun saveDisplayMode(displayMode: DisplayState)
    fun getThemeColor(): Flow<Color?>
    suspend fun saveThemeColor(color: Color?)
    fun getIncomeColor(displayMode: DisplayState): Flow<Color>
    suspend fun saveIncomeColor(color: Color?)
    fun getExpenseColor(displayMode: DisplayState): Flow<Color>
    suspend fun saveExpenseColor(color: Color?)

    fun getTransferColor(displayMode: DisplayState): Flow<Color>
    suspend fun saveTransferColor(color: Color?)

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
    suspend fun saveDailyNotificationTime(dailyNotificationTime: LocalTime)
    fun bioAuthStatus(): Boolean

    // View
    fun getCarryOn(): Flow<Boolean>
    suspend fun saveCarryOn(enableCarryOn: Boolean)
    fun getShowTotal(): Flow<Boolean>
    suspend fun saveShowTotal(enableShowTotal: Boolean)

    // ALL
    fun getAllPreferenceSettings(): Flow<PreferencesState>
    fun getAllPreferenceView(): Flow<FilterState>
}

