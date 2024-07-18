package com.fredy.mysavings.ui.Search

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fredy.mysavings.Feature.Presentation.Navigation.NavigationRoute
import com.fredy.mysavings.Feature.Presentation.Screens.Record.MainScreen.BookAddDialog
import com.fredy.mysavings.Feature.Presentation.Screens.Record.MainScreen.RecordDialog
import com.fredy.mysavings.Feature.Presentation.Screens.Record.Search.SearchBody
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookState
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordState
import com.fredy.mysavings.Feature.Presentation.ViewModels.SearchViewModel.SearchState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavController,
    title: String,
    state: SearchState,
    onSearch: (String) -> Unit,
    recordState: RecordState,
    onEvent: (RecordEvent) -> Unit,
    bookState: BookState,
    bookEvent: (BookEvent) -> Unit,
) {
    BookAddDialog(state = bookState, onEvent = bookEvent)
    recordState.trueRecord?.let {
        RecordDialog(
            trueRecord = it,
            onSaveClicked = { record ->
                onEvent(
                    RecordEvent.DeleteRecord(
                        record
                    )
                )
            },
            onDismissDialog = {
                onEvent(
                    RecordEvent.HideDialog
                )
            },
            onEdit = {
                rootNavController.navigate(
                    "${NavigationRoute.Add.route}?recordId=${it.record.recordId}&bookId=${it.record.bookIdFk}"
                )
            },
        )

    }
    DefaultAppBar(
        modifier = modifier, title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        SearchBar(
            searchText = state.searchQuery,
            onValueChange = {
                onSearch(it)
            },
            isSearching = state.isSearching,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
        ) {
            state.trueRecordsResource.let { resource ->
                ResourceHandler(
                    resource = resource,
                    nullOrEmptyMessage = "You didn't have any records yet",
                    isNullOrEmpty = { it.isNullOrEmpty() },
                    errorMessage = resource.message ?: "",
                    onMessageClick = {
                        rootNavController.navigate(
                            "${NavigationRoute.Add.route}?bookId=${recordState.filterState.currentBook?.bookId}"
                        )
                    },
                    enterTransition = slideInVertically(
                        animationSpec = tween(
                            durationMillis = 500
                        ),
                        initialOffsetY = { fullHeight -> fullHeight },
                    ) + fadeIn(),
                    exitTransition = slideOutVertically(
                        animationSpec = tween(
                            durationMillis = 500
                        ),
                        targetOffsetY = { fullHeight -> fullHeight },
                    ) + fadeOut()
                ) { data ->
                    SearchBody(
                        bookMaps = data,
                        onEvent = onEvent,
                        onBookLongPress = { bookEvent(BookEvent.ShowDialog(it)) },
                        onBookClicked = { bookEvent(BookEvent.ShowDialog(it)) }
                    )
                }
            }
        }

    }
}
