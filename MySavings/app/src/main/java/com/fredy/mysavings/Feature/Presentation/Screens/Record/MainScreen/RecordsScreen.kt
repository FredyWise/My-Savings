package com.fredy.mysavings.Feature.Presentation.Screens.Record.MainScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookState
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookEvent.ShowDialog
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordState
import com.fredy.mysavings.Feature.Presentation.Navigation.NavigationRoute
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ResourceHandler


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordEvent) -> Unit,
    bookState: BookState,
    bookEvent: (BookEvent) -> Unit,
) {
    BookAddDialog(state = bookState, onEvent = bookEvent)
    Column (modifier = modifier){
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
                    selectedItem = state.filterState.currentBook,
                    onBookClicked = {
                        onEvent(RecordEvent.ClickBook(it))
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
                )
            }
        }
    }
}

