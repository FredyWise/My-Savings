package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Database.Enum.FilterType
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ui.Screens.CustomStickyHeader

@Composable
fun FilterDialog(
    modifier: Modifier = Modifier,
    title: String,
    selectedName: String,
    onEvent: (RecordsEvent) -> Unit,
) {
    SimpleAlertDialog(
        modifier = modifier,
        title = title,
        onDismissRequest = {
            onEvent(RecordsEvent.HideFilterDialog)
        },
    ) {
        CustomStickyHeader(
            title = "Filter",
            textStyle = MaterialTheme.typography.titleLarge
        )
        FilterType.values().forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp
                    )
                    .clickable {
                        onEvent(
                            RecordsEvent.FilterRecord(
                                item
                            )
                        )
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier.height(23.dp).padding(end = 8.dp),
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (selectedName == item.name) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    onDismissRequest: () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit = { },
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
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
        dismissButton = dismissButton,
        confirmButton = confirmButton,
    )
}

