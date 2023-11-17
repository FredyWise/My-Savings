package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fredy.mysavings.Data.RoomDatabase.Enum.FilterType
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import com.fredy.mysavings.Data.RoomDatabase.Event.RecordsEvent

@Composable
fun DisplayBar(
    onEvent: (RecordsEvent) -> Unit,
    displayOption: FilterType,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterType.values().forEach { filterType ->
            Row(
                modifier = Modifier.clickable {
                    onEvent(
                        RecordsEvent.SortRecord(
                            filterType
                        )
                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = displayOption == filterType,
                    onClick = {
                        onEvent(
                            RecordsEvent.SortRecord(
                                filterType
                            )
                        )
                    })
                Text(text = filterType.name)
            }
        }
    }
}