package com.fredy.mysavings.ViewModels.Event

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.ChangeColorType
import com.fredy.mysavings.Data.Enum.DisplayState
import com.fredy.mysavings.Data.Enum.FilterType
import java.net.URI
import java.time.LocalDate
import java.time.LocalTime

sealed interface SettingEvent{
    data class SelectDisplayMode(val displayMode: DisplayState): SettingEvent
    object ToggleDailyNotification: SettingEvent
    object ToggleBioAuth: SettingEvent
    object ToggleAutoLogin: SettingEvent
    data class OnExport(val uri: String): SettingEvent
    object ShowColorPallet:SettingEvent
    object HideColorPallet:SettingEvent
    data class ChangeColor(val changeColorType: ChangeColorType,val color: Color): SettingEvent
    data class SetDailyNotificationTime(val time: LocalTime): SettingEvent
    data class SelectStartExportDate(val startDate: LocalDate): SettingEvent
    data class SelectEndExportDate(val endDate: LocalDate): SettingEvent

}