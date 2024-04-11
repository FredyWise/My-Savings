package com.fredy.mysavings.ui.Screens.Record

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fredy.mysavings.ViewModels.BookState
import com.fredy.mysavings.ViewModels.Event.BookEvent
import com.fredy.mysavings.ViewModels.Event.BookEvent.ShowDialog
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
    bookState: BookState,
    bookEvent: (BookEvent) -> Unit,
) {

    BookAddDialog(state = bookState, onEvent = bookEvent)
    Column {
        bookState.bookResource.let { bookResource ->
            ResourceHandler(
                resource = bookResource,
                nullOrEmptyMessage = "There is no book on this date yet",
                isNullOrEmpty = { it.isNullOrEmpty() },
                errorMessage = bookResource.message ?: "",
                onMessageClick = {
                    rootNavController.navigate(
                        "${NavigationRoute.Add.route}?bookId=${state.filterState.currentBook?.bookId}"
                    )
                },
            ) { bookData ->
                RecordHeader(
                    items = bookData,
                    onBookClicked = {
                        onEvent(RecordsEvent.ClickBook(it))
                    },
                    onBookLongPress = {
                        bookEvent(ShowDialog(it))
                    },
                    onAddBook = {
                        bookEvent(ShowDialog(it))
                    },
                )
            }
        }
        state.resourceData.recordMapsResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "There is no record on this date yet",
                isNullOrEmpty = { bookMaps -> bookMaps?.find { it.book == state.filterState.currentBook }?.recordMaps.isNullOrEmpty() },
                errorMessage = resource.message ?: "",
                onMessageClick = {
                    rootNavController.navigate(
                        "${NavigationRoute.Add.route}?bookId=${state.filterState.currentBook?.bookId}"
                    )
                },
            ) { data ->
                RecordBody(
                    recordMaps = data.find { it.book == state.filterState.currentBook }?.recordMaps,
                    onEvent = onEvent,
                    additionalHeader = {
                    }
                )
            }
        }


    }
}

