package com.fredy.mysavings.ui.Screens.Other

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fredy.mysavings.Data.Enum.DisplayState
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.ViewModels.Event.SettingEvent
import com.fredy.mysavings.ViewModels.SettingState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleItem

@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    headerColor: Color = MaterialTheme.colorScheme.primary.copy(0.8f),
    optionColor: Color = MaterialTheme.colorScheme.onBackground,
    spacer: Dp = 5.dp,
    rootNavController: NavController,
    title: String,
    state: SettingState,
    onEvent: (SettingEvent) -> Unit
) {
    DefaultAppBar(
        modifier = modifier,
        title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        CustomStickyHeader(
            textStyle = MaterialTheme.typography.titleLarge,
            title = "Appearance",
            textColor = headerColor,
            useDivider = false
        )
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            menuItems = listOf(
                ActionWithName(
                    DisplayState.Light.name,
                    action = { onEvent(SettingEvent.SelectDisplayMode(DisplayState.Light)) }),
                ActionWithName(
                    DisplayState.Dark.name,
                    action = { onEvent(SettingEvent.SelectDisplayMode(DisplayState.Dark)) }),
                ActionWithName(
                    DisplayState.System.name,
                    action = { onEvent(SettingEvent.SelectDisplayMode(DisplayState.System)) }),
            ),
            endContent = {
                Text(
                    text = state.displayMode.name,
                    color = optionColor,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        ) {
            Text(
                text = "Display Mode",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {

            },
        ) {
            Text(
                text = "Theme Color",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {

            },
        ) {
            Text(
                text = "Expense & Income Color",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }

        CustomStickyHeader(
            textStyle = MaterialTheme.typography.titleLarge,
            title = "Notification",
            textColor = headerColor,
            useDivider = false
        )
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {
                onEvent(SettingEvent.ToggleDailyNotification)
            },
            endContent = {
                Switch(
                    modifier = Modifier.height(10.dp),
                    checked = state.dailyNotification,
                    onCheckedChange = {
                        onEvent(SettingEvent.ToggleDailyNotification)
                    },
                )
            }
        ) {
            Text(
                text = "Daily Notification",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {

            },
        ) {
            Text(
                text = "Notification Settings",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }

        CustomStickyHeader(
            textStyle = MaterialTheme.typography.titleLarge,
            title = "Security",
            textColor = headerColor,
            useDivider = false
        )
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {
                onEvent(SettingEvent.ToggleAutoLogin)
            },
            endContent = {
                Switch(
                    modifier = Modifier.height(10.dp),
                    checked = state.autoLogin,
                    onCheckedChange = { onEvent(SettingEvent.ToggleAutoLogin) },
                )
            }
        ) {
            Text(
                text = "Auto Login",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {
                onEvent(SettingEvent.ToggleBioAuth)
            },
            endContent = {
                Switch(
                    modifier = Modifier.height(10.dp),
                    checked = state.bioAuth,
                    onCheckedChange = {
                        onEvent(SettingEvent.ToggleBioAuth)
                    },
                )
            }
        ) {
            Text(
                text = "Finger Print / Face Recognition",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
    }
}
