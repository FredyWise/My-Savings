package com.fredy.mysavings.Feature.Presentation.Screens.Record.Search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.BookMap
import com.fredy.mysavings.Feature.Presentation.Screens.Record.MainScreen.RecordEntityItem
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.Util.DefaultData
import com.fredy.mysavings.Util.formatDateDay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchBody(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onBookClicked: (Book) -> Unit,
    onBookLongPress: (Book) -> Unit,
    bookMaps: List<BookMap>?,
    onEvent: (RecordsEvent) -> Unit,
) {
    LazyColumn(modifier) {
        bookMaps?.forEach {bookMap ->
            item {
                val book = bookMap.book
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Column(
                        modifier = Modifier
                            .width(80.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .combinedClickable(
                                onLongClick = {
                                    onBookLongPress(book)
                                },
                                onClick = {
                                    onBookClicked(book)
                                },
                            )
                            .padding(
                                vertical = 4.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(
                                    55.dp
                                ),
                            painter = painterResource(
                                id = DefaultData.savingsIcons[book.bookIconDescription]?.image
                                    ?: book.bookIcon
                            ),
                            contentDescription = book.bookIconDescription,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = book.bookName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
            bookMap.recordMaps?.forEach { trueRecordMap ->
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
    }
}