package com.fredy.mysavings.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.Data.Add.BtnAction
import com.fredy.mysavings.Data.Add.dateTimeData
import com.fredy.mysavings.Data.Add.selectOperations
import com.fredy.mysavings.Data.User.Category
import com.fredy.mysavings.ViewModels.addViewModel
import com.fredy.mysavings.ViewModels.calculatorViewModel
import com.fredy.mysavings.ui.component.Add.Calculator
import com.fredy.mysavings.ui.component.Add.ChooseNoteType
import com.fredy.mysavings.ui.component.Add.ChooseWalletAndTag
import com.fredy.mysavings.ui.component.Add.ConfirmationBar
import com.fredy.mysavings.ui.component.Add.DateAndTimePicker
import com.fredy.mysavings.ui.component.Add.TextBox
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    spacing: Dp = 3.dp,
    calculatorViewModel: calculatorViewModel = viewModel(),
    addViewModel: addViewModel = viewModel(),
) {
    val calculatorState = calculatorViewModel.state
    addViewModel.newItem.balance.amount = calculatorState.number1.toFloat()
    val newItem = addViewModel.newItem
    val applicationContext = LocalContext.current
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(8.dp)
    ) {
        ConfirmationBar(onAction = addViewModel::onAction)
        TextBox(
            value = addViewModel.textForTextBox,
            onValueChanged = addViewModel::onTextBoxChange,
            hintText = "Add Note",
            modifier = Modifier
                .fillMaxWidth()
                .weight(
                    1f
                )
        )
        ChooseNoteType(
            selectOperations = selectOperations,
            onAction = addViewModel::onAction,
        )
        ChooseWalletAndTag(spacing = spacing,
            btnTitle = addViewModel.mutableTitle,
            left = newItem.account,
            right = if (newItem.toAccount!= null) {
                newItem.toAccount
            } else {
                newItem.toCategory
            },
            rightBtnAction = BtnAction.ToAccountClicked,
            onAction = addViewModel::onAction)
        Calculator(
            state = calculatorState,
            onAction = calculatorViewModel::onAction,
            btnSpacing = spacing,
            buttonAspectRatio = 1.5f,
            modifier = Modifier.fillMaxWidth()
        )
        DateAndTimePicker(
            applicationContext = applicationContext,
            dateTimeData = addViewModel.pickedDateTime,
            onDateChange = addViewModel::updateDate,
            onTimeChange = addViewModel::updateTime,
            spacing = spacing
        )
    }
}