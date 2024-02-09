package com.fredy.mysavings.ViewModels

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fredy.mysavings.Data.Enum.DisplayState
import com.fredy.mysavings.Data.Notification.NotificationCredentials
import com.fredy.mysavings.Data.Notification.NotificationWorker
import com.fredy.mysavings.Data.Repository.SettingsRepository
import com.fredy.mysavings.ViewModels.Event.SettingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.getDisplayMode(),
                settingsRepository.getAutoLogin(),
                settingsRepository.getBioAuth(),
                settingsRepository.getDailyNotification(),
                settingsRepository.getDailyNotificationTime(),
            ) { displayMode, autoLogin, bioAuth, dailyNotification, notificationTime ->
                SettingState(
                    displayMode = displayMode,
                    autoLogin = autoLogin,
                    bioAuth = bioAuth,
                    dailyNotification = dailyNotification,
                    dailyNotificationTime = notificationTime,
                    updated = true,
                    isBioAuthPossible = bioAuthStatus()
                )
            }.collectLatest { savedState ->
                _state.update { savedState }
            }
        }
    }


    private val _state = MutableStateFlow(
        SettingState()
    )
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SettingState()
    )


    fun onEvent(event: SettingEvent) {

        when (event) {
            is SettingEvent.SelectDisplayMode -> {
                viewModelScope.launch {
                    settingsRepository.saveDisplayMode(event.displayMode)
                }
                _state.update {
                    it.copy(displayMode = event.displayMode)
                }
            }

            is SettingEvent.SelectEndExportDate -> {
                _state.update {
                    it.copy(endDate = event.endDate)
                }
            }

            is SettingEvent.SelectStartExportDate -> {
                _state.update {
                    it.copy(startDate = event.startDate)
                }
            }

            SettingEvent.ToggleAutoLogin -> {
                viewModelScope.launch {
                    settingsRepository.saveAutoLogin(!_state.value.autoLogin)
                }
                _state.update {
                    it.copy(autoLogin = !it.autoLogin)
                }
            }

            SettingEvent.ToggleBioAuth -> {
                viewModelScope.launch {
                    settingsRepository.saveBioAuth(!_state.value.bioAuth)
                }
                _state.update {
                    it.copy(bioAuth = !it.bioAuth)
                }
            }

            SettingEvent.ToggleDailyNotification -> {
                val notificationTime = if (!_state.value.dailyNotification) {
                    _state.value.dailyNotificationTime
                } else {
                    cancelScheduledNotifications()
                    LocalTime.now()
                }
                viewModelScope.launch {
                    settingsRepository.saveDailyNotification(!_state.value.dailyNotification)
                    settingsRepository.saveDailyNotificationTime(notificationTime)
                }
                _state.update {
                    it.copy(
                        dailyNotification = !it.dailyNotification,
                        dailyNotificationTime = notificationTime
                    )
                }
            }

            is SettingEvent.SetDailyNotificationTime -> {
                scheduleNotification(event.time)
                viewModelScope.launch {
                    settingsRepository.saveDailyNotificationTime(event.time)
                }
                _state.update {
                    it.copy(dailyNotificationTime = event.time)
                }
            }

            SettingEvent.OnExport -> {

            }

            SettingEvent.OnBackup -> {

            }

            SettingEvent.OnRestore -> {

            }
        }
    }

    private fun scheduleNotification(time: LocalTime) {// use firebase instead
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1,
            TimeUnit.DAYS
        ).setInitialDelay(
            calculateDelayUntilTime(time),
            TimeUnit.MILLISECONDS
        ).build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(
            NotificationCredentials.DailyNotification.NOTIFICATION_NAME,
            ExistingPeriodicWorkPolicy.REPLACE, workRequest
        )
    }

    private fun cancelScheduledNotifications() {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(NotificationCredentials.DailyNotification.NOTIFICATION_NAME)
    }

    private fun calculateDelayUntilTime(time: LocalTime): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, time.hour)
        calendar.set(Calendar.MINUTE, time.minute)
        calendar.set(Calendar.SECOND, time.second)
        calendar.set(Calendar.MILLISECOND, 0)
        val nextTime = calendar.timeInMillis
        if (nextTime < now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return nextTime - now
    }

    private fun bioAuthStatus(): Boolean {
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
}

data class SettingState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val displayMode: DisplayState = DisplayState.System,
    val autoLogin: Boolean = true,
    val bioAuth: Boolean = false,
    val isBioAuthPossible: Boolean = false,
    val dailyNotification: Boolean = false,
    val dailyNotificationTime: LocalTime = LocalTime.now(),
    val updated: Boolean = false
)