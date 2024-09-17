package com.fredy.book.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fredy.book.viewModel.RecordState
import com.fredy.book.viewModel.RecordEvent
import com.fredy.book.viewModel.BookState
import com.fredy.book.viewModel.BookEvent
import com.fredy.domain.model.Book
import com.fredy.ui.components.handler.ResourceHandler


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    state: RecordState,
    onEvent: (RecordEvent) -> Unit,
    bookState: BookState,
    bookEvent: (BookEvent) -> Unit,
    onAddRecord: () -> Unit,
) {
    BookAddDialog(state = bookState, onEvent = bookEvent)
    Column (modifier = modifier){
        bookState.bookResource.let { bookResource ->
            ResourceHandler(
                resource = bookResource,
                nullOrEmptyMessage = "There is no book on this date yet",
                isNullOrEmpty = { it.isNullOrEmpty() },
                onMessageClick = { bookEvent(BookEvent.ShowDialog(Book(bookName = ""))) },
            ) { bookData ->
                RecordHeader(
                    items = bookData,
                    selectedItem = state.filterState.currentBook,
                    onBookClicked = {
                        onEvent(RecordEvent.ClickBook(it))
                    },
                    onBookLongPress = {
                        bookEvent(BookEvent.ShowDialog(it))
                    },
                    onAddBook = {
                        bookEvent(BookEvent.ShowDialog(it))
                    },
                )
            }
        }
        state.resourceData.recordMapsResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "There is no record on this date yet",
                isNullOrEmpty = { bookMaps -> bookMaps?.find { it.book == state.filterState.currentBook }?.recordMaps.isNullOrEmpty() },
                onMessageClick = onAddRecord,
            ) { data ->
                RecordBody(
                    recordMaps = data.find { it.book == state.filterState.currentBook }?.recordMaps,
                    onEvent = onEvent,
                )
            }
        }
    }
}

