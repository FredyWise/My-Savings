package com.fredy.mysavings.Feature.Data.RepositoryImpl

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fredy.mysavings.Feature.Data.Database.Converter.LocalTimeConverter
import com.fredy.mysavings.Feature.Data.Enum.DisplayMode
import com.fredy.mysavings.Feature.Data.Util.Preferences
import com.fredy.mysavings.Feature.Domain.Repository.PreferencesRepository
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkExpenseColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkIncomeColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkTransferColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultLightExpenseColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultLightIncomeColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultLightTransferColor
import com.fredy.mysavings.Feature.Presentation.ViewModels.PreferencesViewModel.PreferencesState
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.FilterState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(private val context: Context) :
    PreferencesRepository {

    private val preferences = Preferences(context)

    companion object {
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

    override fun getDisplayMode(): Flow<DisplayMode> = preferences.getPreference(DISPLAY_MODE, "System")
        .map { displayMode ->
            DisplayMode.valueOf(displayMode)
        }

    override suspend fun saveDisplayMode(displayMode: DisplayMode) {
        preferences.savePreference(DISPLAY_MODE, displayMode.name)
    }

    override fun getThemeColor(): Flow<Color?> = preferences.getPreference(THEME_COLOR)
        .map { colorCode ->
            colorCode?.let { Color(it) }
        }

    override suspend fun saveThemeColor(color: Color?) {
        preferences.savePreference(THEME_COLOR, color?.toArgb())
    }

    override fun getIncomeColor(displayMode: DisplayMode): Flow<Color> = preferences.getPreference(INCOME_COLOR)
        .map { colorCode ->
            Color(colorCode ?: when (displayMode) {
                DisplayMode.Light -> defaultLightIncomeColor.toArgb()
                DisplayMode.Dark -> defaultDarkIncomeColor.toArgb()
                DisplayMode.System -> defaultLightIncomeColor.toArgb()
            })
        }

    override suspend fun saveIncomeColor(color: Color?) {
        preferences.savePreference(INCOME_COLOR, color?.toArgb())
    }

    override fun getExpenseColor(displayMode: DisplayMode): Flow<Color> = preferences.getPreference(EXPENSE_COLOR)
        .map { colorCode ->
            Color(colorCode ?: when (displayMode) {
                DisplayMode.Light -> defaultLightExpenseColor.toArgb()
                DisplayMode.Dark -> defaultDarkExpenseColor.toArgb()
                DisplayMode.System -> defaultLightExpenseColor.toArgb()
            })
        }

    override suspend fun saveExpenseColor(color: Color?) {
        preferences.savePreference(EXPENSE_COLOR, color?.toArgb())
    }

    override fun getTransferColor(displayMode: DisplayMode): Flow<Color> = preferences.getPreference(TRANSFER_COLOR)
        .map { colorCode ->
            Color(colorCode ?: when (displayMode) {
                DisplayMode.Light -> defaultLightTransferColor.toArgb()
                DisplayMode.Dark -> defaultDarkTransferColor.toArgb()
                DisplayMode.System -> defaultLightTransferColor.toArgb()
            })
        }

    override suspend fun saveTransferColor(color: Color?) {
        preferences.savePreference(TRANSFER_COLOR, color?.toArgb())
    }

    override fun getDailyNotification(): Flow<Boolean> = preferences.getPreference(DAILY_NOTIFICATION, false)

    override suspend fun saveDailyNotification(enableNotification: Boolean) {
        preferences.savePreference(DAILY_NOTIFICATION, enableNotification)
    }

    override fun getAutoLogin(): Flow<Boolean> = preferences.getPreference(AUTO_LOGIN, false)

    override suspend fun saveAutoLogin(enableAutoLogin: Boolean) {
        preferences.savePreference(AUTO_LOGIN, enableAutoLogin)
    }

    override fun getBioAuth(): Flow<Boolean> = preferences.getPreference(BIO_AUTH, false)

    override suspend fun saveBioAuth(enableBioAuth: Boolean) {
        preferences.savePreference(BIO_AUTH, enableBioAuth)
    }

    override fun getCarryOn(): Flow<Boolean> = preferences.getPreference(CARRY_ON, false)

    override suspend fun saveCarryOn(enableCarryOn: Boolean) {
        preferences.savePreference(CARRY_ON, enableCarryOn)
    }

    override fun getShowTotal(): Flow<Boolean> = preferences.getPreference(SHOW_TOTAL, false)

    override suspend fun saveShowTotal(enableShowTotal: Boolean) {
        preferences.savePreference(SHOW_TOTAL, enableShowTotal)
    }

    override fun getDailyNotificationTime(): Flow<LocalTime> = preferences.getPreference(DAILY_NOTIFICATION_TIME, -1)
        .map { time ->
            LocalTimeConverter.intToLocalTime(time)
        }

    override suspend fun saveDailyNotificationTime(dailyNotificationTime: LocalTime) {
        preferences.savePreference(DAILY_NOTIFICATION_TIME, LocalTimeConverter.localTimeToInt(dailyNotificationTime))
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
        val displayMode = getDisplayMode().first()
        val result = PreferencesState(
            displayMode = displayMode,
            autoLogin = getAutoLogin().first(),
            bioAuth = getBioAuth().first(),
            dailyNotification = getDailyNotification().first(),
            dailyNotificationTime = getDailyNotificationTime().first(),
            selectedThemeColor = getThemeColor().first(),
            selectedExpenseColor = getExpenseColor(displayMode).first(),
            selectedIncomeColor = getIncomeColor(displayMode).first(),
            selectedTransferColor = getTransferColor(displayMode).first(),
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
