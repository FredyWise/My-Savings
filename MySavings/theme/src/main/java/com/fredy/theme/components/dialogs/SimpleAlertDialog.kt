package com.fredy.theme.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SimpleAlertDialog(
    modifier: Modifier = Modifier,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    title: String,
    onDismissRequest: () -> Unit,
    leftButton: @Composable (() -> Unit)? = null,
    rightButton: @Composable () -> Unit = { },
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        containerColor = containerColor,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    8.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        },
        onDismissRequest = onDismissRequest,
        dismissButton = leftButton,
        confirmButton = rightButton,
    )
}