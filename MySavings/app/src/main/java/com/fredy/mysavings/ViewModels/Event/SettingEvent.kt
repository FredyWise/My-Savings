package com.fredy.mysavings.ViewModels.Event

import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.DisplayState
import com.fredy.mysavings.Data.Enum.FilterType
import java.time.LocalDate
import java.time.LocalTime

sealed interface SettingEvent{
    data class SelectDisplayMode(val displayMode: DisplayState): SettingEvent
    object ToggleDailyNotification: SettingEvent
    object ToggleBioAuth: SettingEvent
    object ToggleAutoLogin: SettingEvent
    object OnExport: SettingEvent
    object OnBackup: SettingEvent
    object OnRestore: SettingEvent
    object ShowColorPallet:SettingEvent
    object HideColorPallet:SettingEvent
    data class ChangeThemeColor(val color: Color): SettingEvent
    data class ChangeExpenseColor(val color: Color): SettingEvent
    data class ChangeIncomeColor(val color: Color): SettingEvent
    data class SetDailyNotificationTime(val time: LocalTime): SettingEvent
    data class SelectStartExportDate(val startDate: LocalDate): SettingEvent
    data class SelectEndExportDate(val endDate: LocalDate): SettingEvent

}