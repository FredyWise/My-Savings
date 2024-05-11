package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ChooseIcon
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.categoryIcons
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryEvent

@Composable
fun RecordUpdateDialog(
    modifier: Modifier = Modifier,
    record: Record?,
    onDismissRequest: () -> Unit,
    onSaveEffect: (Record) -> Unit,
) {
    val context = LocalContext.current
    record?.let {
        var recordAmount by remember {
            mutableDoubleStateOf(record.recordAmount)
        }
        var recordNote by remember {
            mutableStateOf(record.recordNotes)
        }
        SimpleDialog(
            modifier = modifier,
            dismissOnSave = false,
            title = "Update Record Information",
            onDismissRequest = onDismissRequest,
            onSaveClicked = {
                if (recordNote.isBlank() || recordAmount == 0.0) {
                    Toast.makeText(
                        context,
                        "Please Fill All Required Information!!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onSaveEffect(record.copy(recordAmount = recordAmount, recordNotes = recordNote))
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