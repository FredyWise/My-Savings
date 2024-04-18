package com.fredy.mysavings.Feature.Presentation.Screens.AddSingle

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.formatDateYear
import com.fredy.mysavings.Util.formatTime
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordState
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.AddRecordEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleButton
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


@Composable
fun DateAndTimePicker(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    applicationContext: Context,
    state: AddRecordState,
    onEvent: (AddRecordEvent) -> Unit,
) {
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .height(
                35.dp
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SimpleButton(
            modifier = Modifier.weight(1f),
            onClick = { dateDialogState.show() },
            title = formatDateYear(state.recordDate),
            titleStyle = MaterialTheme.typography.titleLarge.copy(
                onBackground
            )
        )
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
        SimpleButton(
            modifier = Modifier.weight(1f),
            onClick = { timeDialogState.show() },
            title = formatTime(state.recordTime),
            titleStyle = MaterialTheme.typography.titleLarge.copy(
                onBackground
            )
        )
    }
    MaterialDialog(
        dialogState = dateDialogState,
        backgroundColor = MaterialTheme.colorScheme.surface,
        buttons = {
            positiveButton(
                text = "Ok",
                textStyle = TextStyle(
                    MaterialTheme.colorScheme.onSurface
                ),
            ) {
                Toast.makeText(
                    applicationContext,
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
            initialDate = state.recordDate,
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
                onEvent(
                    AddRecordEvent.RecordDate(it)
                )
            },
        )
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
                    applicationContext,
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
            initialTime = state.recordTime,
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
                onEvent(
                    AddRecordEvent.RecordTime(it)
                )
            },
        )
    }
}
