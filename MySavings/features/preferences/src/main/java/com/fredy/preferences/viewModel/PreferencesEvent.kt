package com.fredy.preferences.viewModel


import androidx.compose.ui.graphics.Color
import com.fredy.preferences.data.ChangeColorType
import com.fredy.preferences.data.DisplayMode
import java.time.LocalTime


sealed interface PreferencesEvent {
    data class SelectDisplayMode(val displayMode: DisplayMode) : PreferencesEvent
    data class ChangeColor(
        val changeColorType: ChangeColorType,
        val color: Color?,
        val isSystemDarkTheme: Boolean
    ) : PreferencesEvent

    data class SetDailyNotificationTime(val time: LocalTime): PreferencesEvent

    object ToggleBioAuth : PreferencesEvent
    object ToggleAutoLogin : PreferencesEvent
    object ShowColorPallet : PreferencesEvent
    object HideColorPallet : PreferencesEvent
    object ToggleDailyNotification : PreferencesEvent

}