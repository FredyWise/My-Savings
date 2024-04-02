package com.fredy.mysavings.ViewModels.Event

import androidx.compose.ui.graphics.Color
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.fredy.mysavings.Data.Enum.ChangeColorType
import com.fredy.mysavings.Data.Enum.DisplayState
import java.time.LocalDate
import java.time.LocalTime

sealed interface SettingEvent {
    data class SelectDisplayMode(val displayMode: DisplayState) : SettingEvent
    object ToggleDailyNotification : SettingEvent
    object ToggleBioAuth : SettingEvent
    object ToggleAutoLogin : SettingEvent
    object ShowColorPallet : SettingEvent
    object HideColorPallet : SettingEvent
    data class ChangeColor(val changeColorType: ChangeColorType, val color: Color?) : SettingEvent
    data class SetDailyNotificationTime(val time: LocalTime) : SettingEvent

}