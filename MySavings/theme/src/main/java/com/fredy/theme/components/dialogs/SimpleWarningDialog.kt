package com.fredy.theme.components.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SimpleWarningDialog(
    isShowWarning: Boolean = false,
    onDismissRequest: () -> Unit,
    onSaveClicked: () -> Unit,
    warningText: String
) {
    if (isShowWarning) {
        SimpleDialog(
            title = "Warning!!",
            cancelName = "No",
            saveName = "Yes",
            onDismissRequest = {
                onDismissRequest()
            },
            onSaveClicked = {
                onSaveClicked()
                onDismissRequest()
            },
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = warningText,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}