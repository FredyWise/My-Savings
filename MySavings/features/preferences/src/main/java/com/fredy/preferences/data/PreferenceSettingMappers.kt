package com.fredy.preferences.data


import com.fredy.preferences.domain.PreferenceSettings
import com.fredy.preferences.viewModel.PreferencesState


fun PreferenceSettings.toPreferenceState(): PreferencesState {
    return PreferencesState(
        displayMode = displayMode,
//        isDarkMode = displayMode.isDarkMode(),
        autoLogin = autoLogin,
        bioAuth = bioAuth,
        isBioAuthPossible = isBioAuthPossible,
        dailyNotification = dailyNotification,
        dailyNotificationTime = dailyNotificationTime,
        updated = updated,
    )
}
