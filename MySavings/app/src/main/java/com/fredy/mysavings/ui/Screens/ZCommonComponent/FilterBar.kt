package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun DisplayBar(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    selectedTitle: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onLeadingIconClick: () -> Unit,
    onTrailingIconClick: () -> Unit,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    val dateDialogState = rememberMaterialDialogState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface
            )
            .padding(
                horizontal = 4.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onLeadingIconClick()
                }
                .padding(8.dp),
        ) {
            leadingIcon()
        }
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onPrevious()
                }
                .padding(8.dp),
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "",
            tint = tint,
        )
        SimpleButton(
            modifier = Modifier.weight(1f),
            onClick = { dateDialogState.show() },
            title = selectedTitle,
            titleStyle = MaterialTheme.typography.titleMedium.copy(
                tint
            )
        )
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onNext()
                }
                .padding(8.dp),
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "",
            tint = tint,
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onTrailingIconClick()
                }
                .padding(8.dp),
        ) {
            trailingIcon()
        }
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
            )
            negativeButton(
                text = "Cancel",
                textStyle = TextStyle(
                    MaterialTheme.colorScheme.onSurface
                ),
            )
        },
    ) {
        datepicker(
            initialDate = selectedDate,
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = MaterialTheme.colorScheme.primary,
                headerTextColor = MaterialTheme.colorScheme.onPrimary,
                calendarHeaderTextColor = MaterialTheme.colorScheme.onBackground,
                dateActiveBackgroundColor = MaterialTheme.colorScheme.primary,
                dateInactiveBackgroundColor = Color.Transparent,
                dateActiveTextColor = MaterialTheme.colorScheme.onPrimary,
                dateInactiveTextColor = MaterialTheme.colorScheme.onBackground
            ),
            onDateChange = { onDateChange(it) },
        )
//        val dateRangePickerState = remember {
//            DateRangePickerState(
//                initialSelectedStartDateMillis = selectedDate.atStartOfDay().toMillis(),
//                initialDisplayedMonthMillis = null,
//                initialSelectedEndDateMillis = selectedDate.atStartOfDay().toMillis(),
//                initialDisplayMode = DisplayMode.Picker,
//                yearRange = (2000..2024)
//            )
//        }
//
//        DateRangePicker(state = dateRangePickerState)
    }
}

fun LocalDateTime.toMillis() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
