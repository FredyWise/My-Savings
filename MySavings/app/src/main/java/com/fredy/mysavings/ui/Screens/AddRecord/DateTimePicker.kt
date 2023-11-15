package com.fredy.mysavings.ui.Screens.AddRecord

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
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.RoomDatabase.Event.AddRecordEvent
import com.fredy.mysavings.Data.formatDate
import com.fredy.mysavings.Data.formatTime
import com.fredy.mysavings.ViewModel.AddRecordState
import com.fredy.mysavings.ui.Screens.SimpleButton
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


@Composable
fun DateAndTimePicker(
    modifier: Modifier = Modifier,
    applicationContext: Context,
    state: AddRecordState,
    onEvent: (AddRecordEvent) -> Unit,
) {
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .height(35.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SimpleButton(
            modifier = Modifier
                .weight(1f),
            onClick = { dateDialogState.show() },
            title = formatDate(state.recordDate)
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
            modifier = Modifier
                .weight(1f),
            onClick = { timeDialogState.show() },
            title = formatTime(state.recordTime)
        )
    }
    MaterialDialog(dialogState = dateDialogState,
        backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
        buttons = {
            positiveButton(text = "Ok") {
                Toast.makeText(
                    applicationContext,
                    "Date Changed",
                    Toast.LENGTH_LONG
                ).show()
            }
            negativeButton(text = "Cancel")
        }) {
        datepicker(initialDate = state.recordDate,
            onDateChange = {
                onEvent(
                    AddRecordEvent.RecordDate(it)
                )
            })
    }
    MaterialDialog(dialogState = timeDialogState,
        backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
        buttons = {
            positiveButton(text = "Ok") {
                Toast.makeText(
                    applicationContext,
                    "Time Changed",
                    Toast.LENGTH_LONG
                ).show()
            }
            negativeButton(text = "Cancel")
        }) {
        timepicker(
            initialTime = state.recordTime,
            onTimeChange = {
                onEvent(
                    AddRecordEvent.RecordTime(it)
                )
            }
        )
    }
}
