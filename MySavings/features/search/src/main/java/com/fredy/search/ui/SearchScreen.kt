package com.fredy.search.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fredy.domain.model.Book
import com.fredy.domain.model.TrueRecord
import com.fredy.search.viewModel.SearchState
import com.fredy.theme.components.handler.ResourceHandler
import com.fredy.theme.components.list.SearchBar
import com.fredy.theme.components.navigation.DefaultAppBar


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavController,
    title: String,
    state: SearchState,
    onSearch: (String) -> Unit,
    onShowRecordDialog: (TrueRecord) -> Unit,
    onShowBookDialog: (Book) -> Unit,
    onAddBook: () -> Unit
) {

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
                    onMessageClick = onAddBook,
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
                        onBookLongPress = { onShowBookDialog(it) },
                        onBookClicked = { onShowBookDialog(it) },
                        onRecordItemClicked = { onShowRecordDialog(it) }
                    )
                }
            }
        }

    }
}
