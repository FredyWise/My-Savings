package com.fredy.mysavings.ui.Screens.Other

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fredy.mysavings.ViewModels.Event.SettingEvent
import com.fredy.mysavings.ViewModels.SettingState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SettingButton

@Composable
fun BackupScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavController,
    title: String,
    state: SettingState,
    onEvent: (SettingEvent) -> Unit
) {
    DefaultAppBar(
        modifier = modifier, title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SettingButton(
                modifier = Modifier.weight(0.4f),
                text = "Backup",
                onClick = { onEvent(SettingEvent.OnBackup) },
            )
            Spacer(modifier = Modifier.weight(0.02f))
            SettingButton(
                modifier = Modifier.weight(0.4f),
                text = "Restore",
                onClick = { onEvent(SettingEvent.OnRestore) },
            )
        }
    }
}