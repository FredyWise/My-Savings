package com.fredy.mysavings.ui.Screens.Other

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.common.extensions.isNotNull
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.fredy.mysavings.Util.formatDateYear
import com.fredy.mysavings.ViewModels.DBInfo
import com.fredy.mysavings.ViewModels.Event.IOEvent
import com.fredy.mysavings.ViewModels.IOState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SettingButton
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleDropdown
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
    state: IOState,
    onEvent: (IOEvent) -> Unit
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
    var isImport by remember { mutableStateOf(false) }

    val listOfTitle = listOf("Database Information", "Information", "Information")
    fun title(index: Int): String {
        return when (index) {
            1 -> (if (state.exportDBInfo.takeIf { it.sumOfRecords != 0 }
                    .isNotNull()) "Export " else "Import ")

            2 -> "Import "
            else -> ""
        } + listOfTitle[index]
    }

    val dbInfo = listOfNotNull(
        state.dbInfo,
        state.exportDBInfo.takeIf { it.sumOfRecords != 0 },
        state.importDBInfo.takeIf { it.sumOfRecords != 0 }
    )

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
                onEvent(IOEvent.OnExport(it))
            }
            showDirPicker = false
        }
    )
    var isShowWarning by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }
    FilePicker(show = showFilePicker, fileExtensions = listOf("csv"), onFileSelected = {
        it?.let {
            onEvent(IOEvent.OnImport(it))
            isShowWarning = true
        }
        showFilePicker = false
    })
    if (isShowWarning) {
        SimpleDialog(
            title = "Information", onDismissRequest = { isShowWarning = false },
            onSaveClicked = {
                if (isImport) {
                    onEvent(IOEvent.OnAfterClickedImport)
                } else {
                    if (permissionState) {
                        showDirPicker = true
                    } else {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            permissionLauncher.launch(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }
                    }
                }
                isShowWarning = false
            },
        ) {
            InformationBoard(dbInfo = dbInfo, textColor = onBackground, title = { title(it) })
        }
    }
    DefaultAppBar(
        modifier = modifier, title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SimpleDropdown(
                textFieldShape = RectangleShape,
                list = state.books.map { it.bookName },
                selectedText = state.currentBook.bookName,
                onClick = {
                    onEvent(IOEvent.OnChooseBook(it))
                },
            )
            InformationBoard(
                modifier = Modifier.weight(1f),
                dbInfo = dbInfo,
                textColor = onBackground,
                title = { title(it) },
            )
            Row {
                Spacer(modifier = Modifier.width(8.dp))
                SettingButton(
                    modifier = Modifier
                        .weight(0.4f),
                    text = "Export Now",
                    onClick = {
                        isImport = false
                        onEvent(IOEvent.OnClickedExport)
                        isShowWarning = true
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                SettingButton(
                    modifier = Modifier
                        .weight(0.4f),
                    text = "Import Now",
                    onClick = {
                        isImport = true
                        if (permissionState) {
                            showFilePicker = true
                        } else {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                permissionLauncher.launch(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            }
                        }
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Spacer(modifier = Modifier.height(15.dp))
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
                        .clickable {
                            isImport = false
                            startDateDialogState.show()
                        },
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
                        .clickable {
                            isImport = false
                            endDateDialogState.show()
                        },
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
            Spacer(modifier = Modifier.height(15.dp))
        }
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
                onEvent(IOEvent.SelectStartExportDate(it))
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
                onEvent(IOEvent.SelectEndExportDate(it))
            },
        )
    }
}

@Composable
fun InformationBoard(
    modifier: Modifier = Modifier,
    dbInfo: List<DBInfo>,
    textColor: Color,
    title: (Int) -> String
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(dbInfo) { index, item ->
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                CustomStickyHeader(
                    textStyle = MaterialTheme.typography.titleLarge,
                    title = title(index)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Total Records: ${item.sumOfRecords}", color = textColor)
                Text("Total Accounts: ${item.sumOfAccounts}", color = textColor)
                Text("Total Categories: ${item.sumOfCategories}", color = textColor)
                Text("Total Expenses: ${item.sumOfExpense}", color = textColor)
                Text("Total Income: ${item.sumOfIncome}", color = textColor)
                Text("Total Transfers: ${item.sumOfTransfer}", color = textColor)
            }
        }
        item {
            if (dbInfo.size == 1) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("There is nothing to be exported", color = textColor)
            }
        }
    }
}

