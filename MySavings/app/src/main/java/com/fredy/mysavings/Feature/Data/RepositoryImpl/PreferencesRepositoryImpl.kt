package com.fredy.mysavings.Feature.Data.RepositoryImpl

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fredy.mysavings.Feature.Data.Database.Converter.LocalTimeConverter
import com.fredy.mysavings.Feature.Data.Enum.DisplayState
import com.fredy.mysavings.Feature.Domain.Repository.PreferencesRepository
import com.fredy.mysavings.Feature.Presentation.ViewModels.PreferencesViewModel.PreferencesState
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.FilterState
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkExpenseColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkIncomeColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkTransferColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(private val context: Context) :
    PreferencesRepository {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Settings")
        val DISPLAY_MODE = stringPreferencesKey("display_mode")
        val AUTO_LOGIN = booleanPreferencesKey("auto_login")
        val BIO_AUTH = booleanPreferencesKey("bio_auth")
        val DAILY_NOTIFICATION = booleanPreferencesKey("daily_notification")
        val DAILY_NOTIFICATION_TIME = intPreferencesKey("daily_notification_time")
        val THEME_COLOR = intPreferencesKey("theme_color")
        val EXPENSE_COLOR = intPreferencesKey("expense_color")
        val INCOME_COLOR = intPreferencesKey("income_color")
        val TRANSFER_COLOR = intPreferencesKey("transfer_color")
        val CARRY_ON = booleanPreferencesKey("carry_on")
        val SHOW_TOTAL = booleanPreferencesKey("show_total")
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

    override fun getThemeColor(): Flow<Color?> = context.dataStore.data
        .map { preferences ->
            val colorCode = preferences[THEME_COLOR]
            colorCode?.let {
                Color(colorCode)
            }
        }

    override suspend fun saveThemeColor(color: Color?) {
        context.dataStore.edit { preferences ->
            color?.let {
                preferences[THEME_COLOR] = color.toArgb()
            } ?: preferences.remove(THEME_COLOR)
        }
    }

    override fun getIncomeColor(): Flow<Color> = context.dataStore.data
        .map { preferences ->
            Color(preferences[INCOME_COLOR] ?: defaultDarkIncomeColor.toArgb())
        }

    override suspend fun saveIncomeColor(color: Color) {
        context.dataStore.edit { preferences ->
            preferences[INCOME_COLOR] = color.toArgb()
        }
    }

    override fun getExpenseColor(): Flow<Color> = context.dataStore.data
        .map { preferences ->
            Color(preferences[EXPENSE_COLOR] ?: defaultDarkExpenseColor.toArgb())
        }

    override suspend fun saveExpenseColor(color: Color) {
        context.dataStore.edit { preferences ->
            preferences[EXPENSE_COLOR] = color.toArgb()
        }
    }


    override fun getTransferColor(): Flow<Color> = context.dataStore.data
        .map { preferences ->
            Color(preferences[TRANSFER_COLOR] ?: defaultDarkTransferColor.toArgb())
        }

    override suspend fun saveTransferColor(color: Color) {
        context.dataStore.edit { preferences ->
            preferences[TRANSFER_COLOR] = color.toArgb()
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


    override fun getCarryOn(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[CARRY_ON] ?: false
        }

    override suspend fun saveCarryOn(enableCarryOn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CARRY_ON] = enableCarryOn
        }
    }

    override fun getShowTotal(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_TOTAL] ?: false
        }

    override suspend fun saveShowTotal(enableShowTotal: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_TOTAL] = enableShowTotal
        }
    }

    override fun getDailyNotificationTime(): Flow<LocalTime> = context.dataStore.data
        .map { preferences ->
            LocalTimeConverter.intToLocalTime(preferences[DAILY_NOTIFICATION_TIME] ?: -1)
        }

    override suspend fun saveDailyNotificationTime(dailyNotificationTime: LocalTime) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_NOTIFICATION_TIME] =
                LocalTimeConverter.localTimeToInt(dailyNotificationTime)
        }
    }

    override fun bioAuthStatus(): Boolean {
        val keyGuardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyGuardManager.isDeviceSecure) {
            return true
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    override fun getAllPreferenceSettings(): Flow<PreferencesState> = flow {
        val result = PreferencesState(
            displayMode = getDisplayMode().first(),
            autoLogin = getAutoLogin().first(),
            bioAuth = getBioAuth().first(),
            dailyNotification = getDailyNotification().first(),
            dailyNotificationTime = getDailyNotificationTime().first(),
            selectedThemeColor = getThemeColor().first(),
            selectedExpenseColor = getExpenseColor().first(),
            selectedIncomeColor = getIncomeColor().first(),
            selectedTransferColor = getTransferColor().first(),
            isBioAuthPossible = bioAuthStatus(),
            updated = true
        )
        emit(result)
    }

    override fun getAllPreferenceView(): Flow<FilterState> = flow {
        val result = FilterState(
            carryOn = getCarryOn().first(),
            showTotal = getShowTotal().first(),
        )
        emit(result)
    }


}