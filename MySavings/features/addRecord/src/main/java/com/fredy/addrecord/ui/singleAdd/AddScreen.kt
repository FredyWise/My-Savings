package com.fredy.addrecord.ui.singleAdd

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.addrecord.ui.AddConfirmationRow
import com.fredy.addrecord.ui.AddTextBox
import com.fredy.addrecord.ui.DateAndTimePicker
import com.fredy.addrecord.ui.ImageDialog
import com.fredy.addrecord.ui.LauncherChooserDialog
import com.fredy.addrecord.viewModel.AddRecordEvent
import com.fredy.addrecord.viewModel.AddRecordState
import com.fredy.addrecord.viewModel.singleAdd.CalcEvent
import com.fredy.addrecord.viewModel.singleAdd.CalcState
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.enums.RecordType
import com.fredy.ui.components.button.TypeRadioButton
import com.fredy.ui.components.dialogs.SimpleWarningDialog
import com.fredy.domain.modelUI.ActionWithName


@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    state: AddRecordState,
    onEvent: (AddRecordEvent) -> Unit,
    calculatorState: CalcState,
    onAction: (CalcEvent) -> Unit,
    resource: Resource<AddRecordState, DataError.Local>,
    navigateUp: () -> Unit,
    onSaveClick: () -> Unit,
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit,

    ) {
    val context = LocalContext.current
    var isShowImage by rememberSaveable {
        mutableStateOf(false)
    }
    var capturedImageUri by remember {
        mutableStateOf<Uri>(
            Uri.EMPTY
        )
    }
    ImageDialog(
        isShowImage = isShowImage,
        capturedImageUri = capturedImageUri,
        onDismissRequest = { isShowImage = false },
    )
    var isChoosingLauncher by rememberSaveable {
        mutableStateOf(false)
    }
    LauncherChooserDialog(
        isChoosingLauncher = isChoosingLauncher,
        onDismissRequest = { isChoosingLauncher = false },
        onCapturingImageUri = { capturedImageUri = it },
        detectedText = {
            onEvent(
                AddRecordEvent.RecordNotes(
                    it
                )
            )
        }
    )
    LaunchedEffect(
        key1 = resource,
    ) {
        when (resource) {
            is Resource.Error -> {
                if (!state.isShowWarning) {
                    Toast.makeText(
                        context,
                        (resource as Resource.Error<AddRecordState, DataError.Local>).error.name,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            is Resource.Loading -> {

            }

            is Resource.Success -> {
                Toast.makeText(
                    context,
                    "Record Data Successfully Added",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    SimpleWarningDialog(
        isShowWarning = state.isShowWarning,
        onDismissRequest = { onEvent(AddRecordEvent.DismissWarning) },
        onSaveClicked = {
            onEvent(AddRecordEvent.ConvertCurrency)
        },
        warningText = (resource as Resource.Error<AddRecordState, DataError.Local>).error.name
    )


    Column(
        modifier = modifier
    ) {
        AddConfirmationRow(
            onCancelClick = { navigateUp() },
            onSaveClick = onSaveClick
        )
        AddTextBox(
            value = state.recordNotes,
            onValueChanged = {
                onEvent(
                    AddRecordEvent.RecordNotes(
                        it
                    )
                )
            },
            hintText = "Add Note",
            isImageExist = capturedImageUri != Uri.EMPTY,
            onImageButtonClick = { isShowImage = true },
            onCameraButtonClick = {
                isChoosingLauncher = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(
                    1f
                )
        )
        TypeRadioButton(
            modifier = Modifier.padding(top = 8.dp),
            selectedName = state.recordType.name,
            barHeight = 40.dp,
            radioButtons = listOf(
                ActionWithName(
                    name = RecordType.Expense.name,
                    action = {
                        onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Expense
                            )
                        )
                    },
                ), ActionWithName(
                    name = RecordType.Income.name,
                    action = {
                        onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Income
                            )
                        )
                    },
                ), ActionWithName(
                    name = RecordType.Transfer.name,
                    action = {
                        onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Transfer
                            )
                        )
                    },
                )
            )
        )
        ChooseAccountAndCategory(
            state = state,
            onLeftButtonClick = onLeftButtonClick,
            onRightButtonClick = onRightButtonClick,
        )
        Calculator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            state = calculatorState,
            onAction = onAction,
            textStyle = MaterialTheme.typography.displayMedium,
            buttonAspectRatio = 1.8f,
            leadingObject = {
                Text(
                    modifier = Modifier
                        .weight(
                            0.15f
                        )
                        .padding(8.dp),
                    text = state.recordCurrency,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                )
            },
        )
        DateAndTimePicker(
            applicationContext = context,
            state = state,
            onEvent = onEvent,
        )
    }
}
