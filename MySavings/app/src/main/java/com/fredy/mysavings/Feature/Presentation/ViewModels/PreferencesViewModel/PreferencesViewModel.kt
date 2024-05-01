package com.fredy.mysavings.Feature.Presentation.ViewModels.PreferencesViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fredy.mysavings.Feature.Data.Enum.ChangeColorType
import com.fredy.mysavings.Feature.Data.Enum.DisplayState
import com.fredy.mysavings.Feature.Domain.Notification.NotificationCredentials
import com.fredy.mysavings.Feature.Domain.Notification.NotificationWorker
import com.fredy.mysavings.Feature.Domain.Repository.PreferencesRepository
import com.fredy.mysavings.Feature.Presentation.Util.BalanceColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkExpenseColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkIncomeColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkTransferColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultLightExpenseColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultLightIncomeColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultLightTransferColor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
            preferencesRepository.getAllPreferenceSettings().collect { savedState ->
                BalanceColor.Expense = savedState.selectedExpenseColor!!
                BalanceColor.Income = savedState.selectedIncomeColor!!
                _state.update {
                    savedState
                }
            }
        }
    }

    private val _state = MutableStateFlow(
        PreferencesState()
    )

    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        PreferencesState()
    )

    fun onEvent(event: PreferencesEvent) {
        viewModelScope.launch {
            when (event) {
                is PreferencesEvent.SelectDisplayMode -> {
                    preferencesRepository.saveDisplayMode(event.displayMode)
                    _state.update {
                        it.copy(displayMode = event.displayMode)
                    }
                }

                PreferencesEvent.ToggleAutoLogin -> {
                    preferencesRepository.saveAutoLogin(!_state.value.autoLogin)
                    _state.update {
                        it.copy(autoLogin = !it.autoLogin)
                    }
                }

                PreferencesEvent.ToggleBioAuth -> {
                    preferencesRepository.saveBioAuth(!_state.value.bioAuth)
                    if (!_state.value.bioAuth) {
                        preferencesRepository.saveAutoLogin(true)
                        _state.update {
                            it.copy(autoLogin = true)
                        }
                    }
                    _state.update {
                        it.copy(bioAuth = !it.bioAuth)
                    }
                }

                PreferencesEvent.ToggleDailyNotification -> {
                    val notificationTime = if (!_state.value.dailyNotification) {
                        _state.value.dailyNotificationTime
                    } else {
                        cancelScheduledNotifications()
                        LocalTime.now()
                    }
                    preferencesRepository.saveDailyNotification(!_state.value.dailyNotification)
                    preferencesRepository.saveDailyNotificationTime(notificationTime)
                    _state.update {
                        it.copy(
                            dailyNotification = !it.dailyNotification,
                            dailyNotificationTime = notificationTime
                        )
                    }
                }

                is PreferencesEvent.SetDailyNotificationTime -> {
                    scheduleNotification(event.time)
                    preferencesRepository.saveDailyNotificationTime(event.time)
                    _state.update {
                        it.copy(dailyNotificationTime = event.time)
                    }
                }

                PreferencesEvent.HideColorPallet -> {
                    _state.update {
                        it.copy(isShowColorPallet = false)
                    }
                }

                PreferencesEvent.ShowColorPallet -> {
                    _state.update {
                        it.copy(isShowColorPallet = true)
                    }
                }

                is PreferencesEvent.ChangeColor -> {
                    when (event.changeColorType) {
                        ChangeColorType.Surface -> {
                            preferencesRepository.saveThemeColor(event.color)
                            _state.update {
                                it.copy(selectedThemeColor = event.color)
                            }
                        }

                        ChangeColorType.Income -> {
                            preferencesRepository.saveIncomeColor(event.color)
                            val incomeColor = event.color
                                ?: if (event.isSystemDarkTheme) defaultDarkIncomeColor else defaultLightIncomeColor
                            BalanceColor.Income = incomeColor
                            _state.update {
                                it.copy(selectedIncomeColor = incomeColor)
                            }
                        }

                        ChangeColorType.Expense -> {
                            preferencesRepository.saveExpenseColor(event.color)
                            val expenseColor = event.color
                                ?: if (event.isSystemDarkTheme) defaultDarkExpenseColor else defaultLightExpenseColor
                            BalanceColor.Expense = expenseColor
                            _state.update {
                                it.copy(selectedExpenseColor = expenseColor)
                            }

                        }

                        ChangeColorType.Transfer -> {
                            preferencesRepository.saveTransferColor(event.color)
                            val transferColor = event.color
                                ?: if (event.isSystemDarkTheme) defaultDarkTransferColor else defaultLightTransferColor
                            BalanceColor.Transfer = transferColor
                            _state.update {
                                it.copy(selectedTransferColor = transferColor)
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

