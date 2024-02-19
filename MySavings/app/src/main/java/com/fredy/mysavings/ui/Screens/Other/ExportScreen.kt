package com.fredy.mysavings.ui.Screens.Other

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.fredy.mysavings.Util.formatDateYear
import com.fredy.mysavings.ViewModels.Event.SettingEvent
import com.fredy.mysavings.ViewModels.SettingState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SettingButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExportScreen(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    rootNavController: NavController,
    title: String,
    state: SettingState,
    onEvent: (SettingEvent) -> Unit
) {
    val context = LocalContext.current
    val startDateDialogState = rememberMaterialDialogState()
    val endDateDialogState = rememberMaterialDialogState()
    val permissionState = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        rememberPermissionState(
            permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
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
    var showDirPicker by remember { mutableStateOf(false) }
    DirectoryPicker(
        show = showDirPicker,
        onFileSelected = {
            it?.let {
                onEvent(SettingEvent.OnExport(it))
            }
            showDirPicker = false
        }
    )
    DefaultAppBar(
        modifier = modifier, title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        Row(
            modifier = modifier
                .padding(vertical = 8.dp)
                .height(
                    55.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { startDateDialogState.show() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Start Date",
                    style = MaterialTheme.typography.titleLarge.copy(
                        onBackground
                    ),
                )
                Text(
                    text = formatDateYear(state.startDate.toLocalDate()),
                    style = MaterialTheme.typography.titleLarge.copy(
                        onBackground
                    )
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(
                        2.dp
                    ),
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5f
                )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { endDateDialogState.show() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "End Date",
                    style = MaterialTheme.typography.titleLarge.copy(
                        onBackground
                    ),
                )
                Text(
                    text = formatDateYear(state.endDate.toLocalDate()),
                    style = MaterialTheme.typography.titleLarge.copy(
                        onBackground
                    )
                )
            }
        }
        SettingButton(modifier = Modifier
            .fillMaxWidth(0.7f), text = "Export Now", onClick = {
            if (permissionState) {
                showDirPicker = true
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    permissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }
        })
    }

    MaterialDialog(
        dialogState = startDateDialogState,
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
                    "Date Changed",
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
        datepicker(
            initialDate = state.startDate.toLocalDate(),
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = MaterialTheme.colorScheme.primary,
                headerTextColor = MaterialTheme.colorScheme.onPrimary,
                calendarHeaderTextColor = MaterialTheme.colorScheme.onBackground,
                dateActiveBackgroundColor = MaterialTheme.colorScheme.primary,
                dateInactiveBackgroundColor = Color.Transparent,
                dateActiveTextColor = MaterialTheme.colorScheme.onPrimary,
                dateInactiveTextColor = MaterialTheme.colorScheme.onBackground
            ),
            onDateChange = {
                onEvent(SettingEvent.SelectStartExportDate(it))
            },
        )
    }
    MaterialDialog(
        dialogState = endDateDialogState,
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
                    "Date Changed",
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
        datepicker(
            initialDate = state.endDate.toLocalDate(),
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = MaterialTheme.colorScheme.primary,
                headerTextColor = MaterialTheme.colorScheme.onPrimary,
                calendarHeaderTextColor = MaterialTheme.colorScheme.onBackground,
                dateActiveBackgroundColor = MaterialTheme.colorScheme.primary,
                dateInactiveBackgroundColor = Color.Transparent,
                dateActiveTextColor = MaterialTheme.colorScheme.onPrimary,
                dateInactiveTextColor = MaterialTheme.colorScheme.onBackground
            ),
            onDateChange = {
                onEvent(SettingEvent.SelectEndExportDate(it))
            },
        )
    }
}