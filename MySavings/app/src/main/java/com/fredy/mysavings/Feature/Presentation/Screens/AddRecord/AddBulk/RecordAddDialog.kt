package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleWarningDialog

@Composable
fun RecordAddDialog(
    modifier: Modifier = Modifier,
    isAdding: Boolean = false,
    isShowDialog: Boolean = false,
    record: Record,
    onDismissRequest: () -> Unit,
    onSave: (Record) -> Unit,
    onDelete: (Record) -> Unit,
) {
    val context = LocalContext.current
    if (isShowDialog) {
        var recordAmount by remember {
            mutableDoubleStateOf(record.recordAmount)
        }
        var recordNote by remember {
            mutableStateOf(record.recordNotes)
        }
        var isShowWarning by remember { mutableStateOf(false) }
        SimpleWarningDialog(
            isShowWarning = isShowWarning,
            onDismissRequest = { isShowWarning = false },
            onSaveClicked = {
                onDelete(record)
            },
            warningText = "Are You Sure Want to Delete This Record?"
        )
        SimpleDialog(
            modifier = modifier,
            dismissOnSave = false,
            title = (if (isAdding) "Add" else "Update") + " Record Information",
            additionalExitButton = {
                if (!isAdding) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .clickable { isShowWarning = true }
                                .padding(5.dp),
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete button",
                        )
                    }
                }
            },
            onDismissRequest = onDismissRequest,
            onSaveClicked = {
                if (recordNote.isBlank() || recordAmount == 0.0) {
                    Toast.makeText(
                        context,
                        "Please Fill All Required Information!!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onSave(record.copy(recordAmount = recordAmount, recordNotes = recordNote))
                    onDismissRequest()
                }
            },
        ) {
            TextField(
                value = recordNote,
                onValueChange = {
                    recordNote = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp
                    ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        KeyboardActions.Default.onDone
                    }),
                label = {
                    Text(text = "Notes")
                },
                placeholder = {
                    Text(text = "Record Notes")
                },
            )
            TextField(
                value = recordAmount.toString(),
                onValueChange = {
                    recordAmount = it.toDouble()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp
                    ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        KeyboardActions.Default.onDone
                    }),
                label = {
                    Text(text = "Amounts")
                },
                placeholder = {
                    Text(text = "Record Amounts")
                },
            )
        }
    }
}