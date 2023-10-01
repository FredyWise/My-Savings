package com.fredy.mysavings.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.ViewModels.addViewModel
import com.fredy.mysavings.ViewModels.calculatorViewModel
import com.fredy.mysavings.ViewModels.dateAndTimeViewModel
import com.fredy.mysavings.ui.component.Add.Calculator
import com.fredy.mysavings.ui.component.Add.ChooseNoteType
import com.fredy.mysavings.ui.component.Add.ChooseWalletAndTag
import com.fredy.mysavings.ui.component.Add.ConfirmationBar
import com.fredy.mysavings.ui.component.Add.DateAndTimePicker
import com.fredy.mysavings.ui.component.Add.TextBox

@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    spacing: Dp = 3.dp,
    calculatorViewModel: calculatorViewModel = viewModel(),
    addViewModel: addViewModel = viewModel(),
    dateAndTimeViewModel: dateAndTimeViewModel = viewModel(),
    applicationContext: Context
) {
    Column(
        modifier = modifier.background(
            MaterialTheme.colorScheme.background
        ).padding(6.dp)
    ) {
        ConfirmationBar(onAction = addViewModel::onAction)
        TextBox(
            value = addViewModel.textForTextBox,
            onValueChanged = {
                addViewModel.onTextBoxChange(it)
            },
            hintText = "Add Note",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        ChooseNoteType(
            isSelected = addViewModel.select,
            onAction = addViewModel::onAction
        )
        ChooseWalletAndTag(spacing = spacing,
            onClickBtn1 = {},
            onClickBtn2 = {})
        Calculator(
            state = calculatorViewModel.state,
            onAction = calculatorViewModel::onAction,
            btnSpacing = spacing,
            buttonAspectRatio = 1.5f,
            modifier = Modifier.fillMaxWidth()
        )
        DateAndTimePicker(
            dateDialogState = dateAndTimeViewModel.dateDialogState,
            timeDialogState = dateAndTimeViewModel.timeDialogState,
            formattedDate = dateAndTimeViewModel.formattedDate,
            formattedTime = dateAndTimeViewModel.formattedTime,
            onDateChange = {
                dateAndTimeViewModel.updateDate(it)
            },
            onTimeChange = {
                dateAndTimeViewModel.updateTime(it)
            },
            applicationContext = applicationContext,
            spacing = spacing
        )
    }


}