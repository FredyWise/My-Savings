package com.fredy.mysavings.ui.Screens.AddRecord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.Event.AddRecordEvent
import com.fredy.mysavings.ViewModel.AddRecordViewModel
import com.fredy.mysavings.ViewModel.AddRecordViewModelFactory
import com.fredy.mysavings.ViewModel.CalculatorViewModel
import com.fredy.mysavings.ui.ActionWithName
import com.fredy.mysavings.ui.SimpleButton
import com.fredy.mysavings.ui.TypeRadioButton

@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    id: Int,
    navigateUp: () -> Unit,
    calculatorViewModel: CalculatorViewModel = viewModel(),
    viewModel:AddRecordViewModel = viewModel(factory = AddRecordViewModelFactory(id))
) {
    val state by viewModel.state.collectAsState()
    val calculatorState = calculatorViewModel.state
    val applicationContext = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SimpleButton(
                modifier = Modifier,
                onClick = { navigateUp() },
                imageVector = Icons.Default.Close,
                title = "CANCEL",
                titleStyle = MaterialTheme.typography.titleMedium,
            )
            SimpleButton(
                onClick = {
                    viewModel.onEvent(
                        AddRecordEvent.SaveRecord
                    )
                },
                imageVector = Icons.Default.Check,
                title = "SAVE",
                titleStyle = MaterialTheme.typography.titleMedium,
            )
        }
        TextBox(
            value = state.recordNotes,
            onValueChanged = {
                viewModel.onEvent(
                    AddRecordEvent.RecordNotes(it)
                )
            },
            hintText = "Add Note",
            modifier = Modifier
                .fillMaxWidth()
                .weight(
                    1f
                )
        )
        TypeRadioButton(
            selectedName = state.recordType.name,
            radioButtons = listOf(
                ActionWithName(
                    name = RecordType.Expense.name,
                    action = {
                        viewModel.onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Expense
                            )
                        )
                    },
                ), ActionWithName(
                    name = RecordType.Income.name,
                    action = {
                        viewModel.onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Income
                            )
                        )
                    },
                ), ActionWithName(
                    name = RecordType.Transfer.name,
                    action = {
                        viewModel.onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Transfer
                            )
                        )
                    },
                )
            )
        )
//        ChooseWalletAndTag (spacing = spacing,
//        btnTitle = addViewModel.mutableTitle,
//        left = newItem.account,
//        right = if (newItem.toAccount != null) {
//            newItem.toAccount
//        } else {
//            newItem.toCategory
//        },
//        rightBtnAction = BtnAction.ToAccountClicked,
//        onAction = addViewModel::onAction
//        )
        Calculator(
            state = calculatorState,
            onAction = calculatorViewModel::onAction,
            buttonAspectRatio = 1.5f,
            modifier = Modifier.fillMaxWidth()
        )
        DateAndTimePicker(
            applicationContext = applicationContext,
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}

