package com.fredy.mysavings.Feature.Presentation.Screens.Record.MainScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Util.formatDateDay
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Domain.Model.RecordMap
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.CustomStickyHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordBody(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    recordMaps: List<RecordMap>?,
    onEvent: (RecordEvent) -> Unit,
    additionalHeader: @Composable () -> Unit = {},
) {
    LazyColumn(modifier) {
        item {
            additionalHeader()
        }
        recordMaps?.forEach { trueRecordMap ->
            stickyHeader {
                CustomStickyHeader(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.background
                    ),
                    title = formatDateDay(
                        trueRecordMap.recordDate
                    ),
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            items(trueRecordMap.records, key = { it.record.recordId }) { item ->
                Divider(
                    modifier = Modifier.height(0.3.dp),
                    color = onBackgroundColor.copy(
                        alpha = 0.4f
                    )
                )
                RecordEntityItem(
                    backgroundColor = backgroundColor,
                    onBackgroundColor = onBackgroundColor,
                    item = item,
                    onItemClick = {
                        onEvent(
                            RecordEvent.ShowDialog(
                                item
                            )
                        )
                    },
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(75.dp))
        }

    }
}
