package com.fredy.mysavings.ViewModels

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fredy.mysavings.Data.CSV.CSVDao
import com.fredy.mysavings.Data.Enum.ChangeColorType
import com.fredy.mysavings.Data.Enum.DisplayState
import com.fredy.mysavings.Data.Notification.NotificationCredentials
import com.fredy.mysavings.Data.Notification.NotificationWorker
import com.fredy.mysavings.Data.Repository.CSVRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Data.Repository.SettingsRepository
import com.fredy.mysavings.Data.Repository.SyncRepository
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.defaultExpenseColor
import com.fredy.mysavings.Util.defaultIncomeColor
import com.fredy.mysavings.Util.defaultTransferColor
import com.fredy.mysavings.Util.formatDateYear
import com.fredy.mysavings.Util.initialDarkThemeDefaultColor
import com.fredy.mysavings.Util.isInternetConnected
import com.fredy.mysavings.ViewModels.Event.SettingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val recordRepository: RecordRepository,
    private val syncRepository: SyncRepository,
    private val csvRepository: CSVRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
            settingsRepository.getAllPreferenceSettings().collect { savedState ->
                BalanceColor.Expense = savedState.selectedExpenseColor
                BalanceColor.Income = savedState.selectedIncomeColor
                _state.update {
                    savedState
                }
            }
            if (isInternetConnected(context)) {
                syncRepository.syncAll()
            }
        }
    }

    private val _state = MutableStateFlow(
        SettingState()
    )
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(10000),
        SettingState()
    )

    fun onEvent(event: SettingEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingEvent.SelectDisplayMode -> {
                    settingsRepository.saveDisplayMode(event.displayMode)
                    _state.update {
                        it.copy(displayMode = event.displayMode)
                    }
                }

                SettingEvent.ToggleAutoLogin -> {
                    settingsRepository.saveAutoLogin(!_state.value.autoLogin)
                    _state.update {
                        it.copy(autoLogin = !it.autoLogin)
                    }
                }

                SettingEvent.ToggleBioAuth -> {
                    settingsRepository.saveBioAuth(!_state.value.bioAuth)
                    if (!_state.value.bioAuth){
                        settingsRepository.saveAutoLogin(true)
                        _state.update {
                            it.copy(autoLogin = true)
                        }
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
                    settingsRepository.saveDailyNotification(!_state.value.dailyNotification)
                    settingsRepository.saveDailyNotificationTime(notificationTime)
                    _state.update {
                        it.copy(
                            dailyNotification = !it.dailyNotification,
                            dailyNotificationTime = notificationTime
                        )
                    }
                }

                is SettingEvent.SetDailyNotificationTime -> {
                    scheduleNotification(event.time)
                    settingsRepository.saveDailyNotificationTime(event.time)
                    _state.update {
                        it.copy(dailyNotificationTime = event.time)
                    }
                }

                is SettingEvent.SelectEndExportDate -> {
                    _state.update {
                        it.copy(endDate = LocalDateTime.of(event.endDate, LocalTime.MAX))
                    }
                }

                is SettingEvent.SelectStartExportDate -> {
                    _state.update {
                        it.copy(startDate = LocalDateTime.of(event.startDate, LocalTime.MIN))
                    }
                }

                is SettingEvent.OnExport -> {
                    val startDate = state.value.startDate
                    val endDate = state.value.endDate
                    recordRepository.getAllTrueRecordsWithinSpecificTime(startDate, endDate)
                        .collectLatest { recordsResource ->
                            when (recordsResource) {
                                is Resource.Error -> {}
                                is Resource.Loading -> {}
                                is Resource.Success -> {
                                    csvRepository.outputToCSV(
                                        event.uri,
                                        formatDateYear(startDate.toLocalDate()) + "- " + formatDateYear(
                                            endDate.toLocalDate()
                                        ),
                                        recordsResource.data!!
                                    )
                                }
                            }
                        }
                }


                is SettingEvent.OnImport -> {
                    val result = csvRepository.inputFromCSV(
                        event.uri.path,
                    )
                    Log.e(TAG, "onEvent: $result",)
                }

                SettingEvent.HideColorPallet -> {
                    _state.update {
                        it.copy(isShowColorPallet = false)
                    }
                }

                SettingEvent.ShowColorPallet -> {
                    _state.update {
                        it.copy(isShowColorPallet = true)
                    }
                }

                is SettingEvent.ChangeColor -> {
                    when (event.changeColorType) {
                        ChangeColorType.Surface -> {
                            settingsRepository.saveThemeColor(event.color)
                            _state.update {
                                it.copy(selectedThemeColor = event.color)
                            }
                        }

                        ChangeColorType.Income -> {
                            event.color?.let {
                                settingsRepository.saveIncomeColor(event.color)
                                BalanceColor.Income = event.color
                                _state.update {
                                    it.copy(selectedIncomeColor = event.color)
                                }
                            }
                        }

                        ChangeColorType.Expense -> {
                            event.color?.let {
                                settingsRepository.saveExpenseColor(event.color)
                                BalanceColor.Expense = event.color
                                _state.update {
                                    it.copy(selectedExpenseColor = event.color)
                                }
                            }
                        }

                        ChangeColorType.Transfer -> {
                            event.color?.let {
                                settingsRepository.saveTransferColor(event.color)
                                BalanceColor.Transfer = event.color
                                _state.update {
                                    it.copy(selectedTransferColor = event.color)
                                }
                            }
                        }
                    }
                }
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

}

data class SettingState(
    val startDate: LocalDateTime = LocalDateTime.now(),
    val endDate: LocalDateTime = LocalDateTime.now(),
    val displayMode: DisplayState = DisplayState.System,
    val autoLogin: Boolean = true,
    val bioAuth: Boolean = false,
    val isBioAuthPossible: Boolean = false,
    val isShowColorPallet: Boolean = false,
    val selectedThemeColor: Color? = null,
    val selectedExpenseColor: Color = defaultExpenseColor,
    val selectedIncomeColor: Color = defaultIncomeColor,
    val selectedTransferColor: Color = defaultTransferColor,
    val dailyNotification: Boolean = false,
    val dailyNotificationTime: LocalTime = LocalTime.now(),
    val updated: Boolean = false,
)