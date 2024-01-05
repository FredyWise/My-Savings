package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleButton
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun DisplayBar(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    selectedTitle: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
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
                horizontal = 4.dp, vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon()
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
            onClick = { dateDialogState.show() },
            title = selectedTitle,
            titleStyle = MaterialTheme.typography.titleLarge.copy(
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
        trailingIcon()
    }

    MaterialDialog(dialogState = dateDialogState,
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
        }) {
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
    }
}
