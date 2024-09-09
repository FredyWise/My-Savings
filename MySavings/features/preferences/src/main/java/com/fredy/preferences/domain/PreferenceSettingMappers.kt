package com.fredy.preferences.domain


import com.fredy.preferences.data.DisplayMode
import com.fredy.preferences.viewModel.PreferencesState


fun PreferenceSettings.toPreferenceState(): PreferencesState {
    return PreferencesState(
//        displayMode = displayMode,
        isDarkMode = displayMode.isDarkMode(),
        autoLogin = autoLogin,
        bioAuth = bioAuth,
        isBioAuthPossible = isBioAuthPossible,
        dailyNotification = dailyNotification,
        dailyNotificationTime = dailyNotificationTime,
        updated = updated,
    )
}

fun DisplayMode.isDarkMode(): Boolean? {
    return when (this) {
        DisplayMode.Light -> false
        DisplayMode.Dark -> true
        DisplayMode.System -> null
    }
}