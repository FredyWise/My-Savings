package com.fredy.mysavings.ui.Screens.Record

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
import com.fredy.mysavings.Util.formatDateDay
import com.fredy.mysavings.ViewModels.BookMap
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CustomStickyHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordBody(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    bookMaps: List<BookMap>,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
) {
    LazyColumn(modifier) {
        item {
            RecordHeader(items = bookMaps, onBookClicked = {}, onAddBook = {})
        }
        bookMaps.firstOrNull { it.book == state.filterState.currentBook }?.let { bookMap ->
            bookMap.recordMaps.forEach { trueRecordMap ->
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
                                RecordsEvent.ShowDialog(
                                    item
                                )
                            )
                        },
                    )
                }
            }

        }
        item {
            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}
