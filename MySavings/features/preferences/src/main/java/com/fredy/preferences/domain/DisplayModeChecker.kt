package com.fredy.preferences.domain




fun DisplayMode.isDarkMode(): Boolean? {
    return when (this) {
        DisplayMode.Light -> false
        DisplayMode.Dark -> true
        DisplayMode.System -> null
    }
}
