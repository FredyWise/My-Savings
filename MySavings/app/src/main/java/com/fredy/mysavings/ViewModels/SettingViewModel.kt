package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Enum.DisplayState
import com.fredy.mysavings.ViewModels.Event.SettingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingState()
    )
    private val _testing = MutableStateFlow(
        SettingState()
    )

    val state = combine(
        _state,
        _testing,
//        _availableCurrency,
//        balanceBar,
//        _filterState,
    ) { state, recordResource ->//, availableCurrency, balanceBar, filterState ->
        state.copy(

        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SettingState()
    )


    fun onEvent(event: SettingEvent) {

        when (event) {
            is SettingEvent.SelectDisplayMode -> {
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
                _state.update {
                    it.copy(autoLogin = !it.autoLogin)
                }
            }

            SettingEvent.ToggleBioAuth -> {
                _state.update {
                    it.copy(bioAuth = !it.bioAuth)
                }
            }

            SettingEvent.ToggleDailyNotification -> {
                _state.update {
                    it.copy(dailyNotification = !it.dailyNotification)
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
}

data class SettingState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val displayMode: DisplayState = DisplayState.System,
    val autoLogin: Boolean = true,
    val bioAuth: Boolean = false,
    val dailyNotification: Boolean = false,
)