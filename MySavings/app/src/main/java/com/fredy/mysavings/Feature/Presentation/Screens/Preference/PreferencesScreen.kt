package com.fredy.mysavings.Feature.Presentation.Screens.Preference

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fredy.mysavings.Feature.Data.Enum.ChangeColorType
import com.fredy.mysavings.Feature.Data.Enum.DisplayState
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.Util.defaultDarkExpenseColor
import com.fredy.mysavings.Util.defaultDarkIncomeColor
import com.fredy.mysavings.Util.defaultDarkTransferColor
import com.fredy.mysavings.Util.defaultLightExpenseColor
import com.fredy.mysavings.Util.defaultLightIncomeColor
import com.fredy.mysavings.Util.defaultLightTransferColor
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatTime
import com.fredy.mysavings.Util.initialDarkThemeDefaultColor
import com.fredy.mysavings.Util.initialLightThemeDefaultColor
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.SettingEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.SettingState
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleButton
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    headerColor: Color = MaterialTheme.colorScheme.primary.copy(0.8f),
    optionColor: Color = MaterialTheme.colorScheme.onBackground,
    isSystemDarkTheme: Boolean = isSystemInDarkTheme(),
    spacer: Dp = 3.dp,
    rootNavController: NavController,
    title: String,
    state: SettingState,
    onEvent: (SettingEvent) -> Unit
) {
    val context = LocalContext.current
    val timeDialogState = rememberMaterialDialogState()
    val permissionsState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        ).status.isGranted
    } else {
        true
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                context,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Permission Denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    var selectedColorType by remember {
        mutableStateOf(ChangeColorType.Surface)
    }
    var initialColor by remember {
        mutableStateOf(
            when (selectedColorType) {
                ChangeColorType.Surface -> {
                    state.selectedThemeColor
                        ?: if (isSystemDarkTheme) initialDarkThemeDefaultColor else initialLightThemeDefaultColor
                }
                ChangeColorType.Income -> state.selectedIncomeColor
                ChangeColorType.Expense -> state.selectedExpenseColor
                ChangeColorType.Transfer -> state.selectedTransferColor
            }
        )
    }
    var selectedColor by remember {
        mutableStateOf<Color?>(
            initialColor
        )
    }

    if (state.isShowColorPallet) {
        SimpleDialog(
            title = "Color Picker",
            onDismissRequest = { onEvent(SettingEvent.HideColorPallet) },
            onSaveClicked = {
                onEvent(SettingEvent.ChangeColor(selectedColorType, selectedColor))
            },
        ) {
            ColorPicker(onColorChange = { selectedColor = it }, initialColor = initialColor)
            SimpleButton(onClick = {
                val isSystemDarkThemes = isSystemDarkTheme && state.displayMode == DisplayState.System
                val isDisplayDark = state.displayMode == DisplayState.Dark
                selectedColor = when (selectedColorType) {
                    ChangeColorType.Surface -> null
                    ChangeColorType.Income -> if (isSystemDarkThemes || isDisplayDark ) defaultDarkIncomeColor else defaultLightIncomeColor
                    ChangeColorType.Expense -> if (isSystemDarkThemes || isDisplayDark ) defaultDarkExpenseColor else defaultLightExpenseColor
                    ChangeColorType.Transfer ->if (isSystemDarkThemes || isDisplayDark ) defaultDarkTransferColor else defaultLightTransferColor
                }
            }, title = "Set Back To Initial Color")
        }
    }

    DefaultAppBar(
        modifier = modifier,
        contentModifier = Modifier.verticalScroll(rememberScrollState()),
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
                selectedColorType = ChangeColorType.Surface
                onEvent(SettingEvent.ShowColorPallet)
            },
            endContent = {
                Box(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
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
                selectedColorType = ChangeColorType.Expense
                onEvent(SettingEvent.ShowColorPallet)
            },
            endContent = {
                Text(
                    text = formatBalanceAmount(3.33, "USD"),
                    color = state.selectedExpenseColor,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        ) {
            Text(
                text = "Expense Color",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {
                selectedColorType = ChangeColorType.Income
                onEvent(SettingEvent.ShowColorPallet)
            },
            endContent = {
                Text(
                    text = formatBalanceAmount(3.33, "USD"),
                    color = state.selectedIncomeColor,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        ) {
            Text(
                text = "Income Color",
                color = optionColor,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {
                selectedColorType = ChangeColorType.Transfer
                onEvent(SettingEvent.ShowColorPallet)
            },
            endContent = {
                Text(
                    text = formatBalanceAmount(3.33, "USD"),
                    color = state.selectedTransferColor,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        ) {
            Text(
                text = "Income Color",
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
                if (permissionsState) {
                    onEvent(SettingEvent.ToggleDailyNotification)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }
            },
            endContent = {
                Switch(
                    modifier = Modifier.height(10.dp),
                    checked = state.dailyNotification,
                    onCheckedChange = {
                        if (permissionsState) {
                            onEvent(SettingEvent.ToggleDailyNotification)
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        }
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
        AnimatedVisibility(visible = state.dailyNotification) {
            Spacer(modifier = Modifier.height(spacer))
            SimpleItem(
                onClick = {
                    timeDialogState.show()
                },
                endContent = {
                    Text(
                        text = formatTime(state.dailyNotificationTime), color = optionColor
                    )
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            ) {
                Text(
                    text = "Reminder Time",
                    color = optionColor,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
                )
            }
        }
        Spacer(modifier = Modifier.height(spacer))
        SimpleItem(
            onClick = {
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context.packageName, null)
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(
                        context,
                        "Notification settings not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
                if (state.isBioAuthPossible) {
                    onEvent(SettingEvent.ToggleBioAuth)
                } else {
                    Toast.makeText(
                        context,
                        "Bio authentication is not available on your device",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            endContent = {
                Switch(
                    modifier = Modifier.height(10.dp),
                    checked = state.bioAuth,
                    onCheckedChange = {
                        if (state.isBioAuthPossible) {
                            onEvent(SettingEvent.ToggleBioAuth)
                        } else {
                            Toast.makeText(
                                context,
                                "Bio authentication is not available on your device",
                                Toast.LENGTH_LONG
                            ).show()
                        }
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



    MaterialDialog(
        dialogState = timeDialogState,
        backgroundColor = MaterialTheme.colorScheme.surface,
        buttons = {
            positiveButton(
                text = "Ok",
                textStyle = TextStyle(
                    MaterialTheme.colorScheme.onSurface
                ),
            ) {
                Toast.makeText(
                    context,
                    "Time Changed",
                    Toast.LENGTH_LONG
                ).show()
            }
            negativeButton(
                text = "Cancel",
                textStyle = TextStyle(
                    MaterialTheme.colorScheme.onSurface
                ),
            )
        },
    ) {
        timepicker(
            initialTime = state.dailyNotificationTime,
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = MaterialTheme.colorScheme.primary,
                inactiveBackgroundColor = MaterialTheme.colorScheme.secondary.copy(
                    0.6f
                ),
                activeTextColor = MaterialTheme.colorScheme.onPrimary,
                inactiveTextColor = MaterialTheme.colorScheme.onSurface.copy(
                    0.9f
                ),
                inactivePeriodBackground = MaterialTheme.colorScheme.secondary.copy(
                    0.6f
                ),
                selectorColor = MaterialTheme.colorScheme.primary,
                selectorTextColor = MaterialTheme.colorScheme.onPrimary,
                headerTextColor = MaterialTheme.colorScheme.onBackground,
                borderColor = MaterialTheme.colorScheme.onBackground
            ),
            onTimeChange = {
                onEvent(SettingEvent.SetDailyNotificationTime(it))
            },
        )
    }

}


