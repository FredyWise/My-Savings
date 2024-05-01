package com.fredy.mysavings.Feature.Presentation.ViewModels.PreferencesViewModel

import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Feature.Data.Enum.ChangeColorType
import com.fredy.mysavings.Feature.Data.Enum.DisplayState
import java.time.LocalTime

sealed interface PreferencesEvent {
    data class SelectDisplayMode(val displayMode: DisplayState) : PreferencesEvent
    object ToggleDailyNotification : PreferencesEvent
    object ToggleBioAuth : PreferencesEvent
    object ToggleAutoLogin : PreferencesEvent
    object ShowColorPallet : PreferencesEvent
    object HideColorPallet : PreferencesEvent
    data class ChangeColor(val changeColorType: ChangeColorType, val color: Color?, val isSystemDarkTheme:Boolean) : PreferencesEvent
    data class SetDailyNotificationTime(val time: LocalTime) : PreferencesEvent

}