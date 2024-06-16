package com.fredy.mysavings.Feature.Presentation.ViewModels.PreferencesViewModel

import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Feature.Data.Enum.DisplayMode
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkExpenseColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkIncomeColor
import com.fredy.mysavings.Feature.Presentation.Util.defaultDarkTransferColor
import java.time.LocalDateTime
import java.time.LocalTime

data class PreferencesState(
    val startDate: LocalDateTime = LocalDateTime.now(),
    val endDate: LocalDateTime = LocalDateTime.now(),
    val displayMode: DisplayMode = DisplayMode.System,
    val autoLogin: Boolean = false,
    val bioAuth: Boolean = false,
    val isBioAuthPossible: Boolean = false,
    val isShowColorPallet: Boolean = false,
    val selectedThemeColor: Color? = null,
    val selectedExpenseColor: Color = defaultDarkExpenseColor,
    val selectedIncomeColor: Color = defaultDarkIncomeColor,
    val selectedTransferColor: Color = defaultDarkTransferColor,
    val dailyNotification: Boolean = false,
    val dailyNotificationTime: LocalTime = LocalTime.now(),
    val updated: Boolean = false,
)