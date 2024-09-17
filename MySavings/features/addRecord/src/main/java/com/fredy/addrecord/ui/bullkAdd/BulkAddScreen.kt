package com.fredy.addrecord.ui.bullkAdd

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fredy.addrecord.ui.AddConfirmationRow
import com.fredy.addrecord.ui.AddTextBox
import com.fredy.addrecord.ui.DateAndTimePicker
import com.fredy.addrecord.ui.ImageDialog
import com.fredy.addrecord.ui.LauncherChooserDialog
import com.fredy.addrecord.viewModel.AddRecordEvent
import com.fredy.addrecord.viewModel.AddRecordState
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.model.Record


@Composable
fun BulkAddScreen(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    state: AddRecordState,
    onEvent: (AddRecordEvent) -> Unit,
    resource: Resource<AddRecordState, DataError.Local>,
    navigateUp: () -> Unit,
    onConvertImageToRecord: (Uri) -> Unit,
    onRecordNotesChange: (String) -> Unit,
    onShowAddRecordItemDialog: (Record) -> Unit,
    onSaveRecordClick: () -> Unit,
    onTopButtonClick: () -> Unit,
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
        onCapturingImageUri = {
            capturedImageUri = it
            onConvertImageToRecord(it)
        },
        detectedText = {
            onRecordNotesChange(it)
        }
    )

    LaunchedEffect(
        key1 = resource,
    ) {
        when (resource) {
            is Resource.Error -> {
                Toast.makeText(
                    context,
                    resource.error.name,
                    Toast.LENGTH_LONG
                ).show()
            }

            is Resource.Loading -> {

            }

            is Resource.Success -> {
                Toast.makeText(
                    context,
                    "Record Successfully Added",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddConfirmationRow(
            onCancelClick = { navigateUp() },
            onSaveClick = onSaveRecordClick,
        )
        AddTextBox(
            value = state.recordNotes,
            onValueChanged = onRecordNotesChange,
            hintText = "Text extraction result will show here",
            isImageExist = capturedImageUri != Uri.EMPTY,
            onImageButtonClick = { isShowImage = true },
            onCameraButtonClick = {
                isChoosingLauncher = true
            },
            modifier = Modifier.heightIn(100.dp, 150.dp)
        )
        ChooseAccountAndMultiCategory(
            state = state,
            onTopButtonClick = onTopButtonClick,
            onLeftButtonClick = onLeftButtonClick,
            onRightButtonClick = onRightButtonClick,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .weight(
                    1f
                ),
            verticalArrangement = Arrangement.Center
        ) {
            if (capturedImageUri != Uri.EMPTY) {
                RecordList(
                    Modifier
                        .weight(
                            1f
                        ),
                    records = state.records,
                    onItemClick = onShowAddRecordItemDialog
                )
            } else {
                Icon(
                    contentDescription = "Captured Image",
                    imageVector = Icons.Default.AddAPhoto,
                    tint = onBackground,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            isChoosingLauncher = true
                        }
                        .padding(20.dp)
                        .size(100.dp)
                )
            }
        }
        DateAndTimePicker(
            applicationContext = context,
            state = state,
            onEvent = onEvent,
        )
    }
}