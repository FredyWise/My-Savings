package com.fredy.mysavings.ui.Search

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fredy.mysavings.Util.DefaultData
import com.fredy.mysavings.ViewModels.BookState
import com.fredy.mysavings.ViewModels.Event.BookEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ViewModels.SearchState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.Record.RecordBody
import com.fredy.mysavings.ui.Screens.Record.RecordDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SearchBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavController,
    title: String,
    state: SearchState,
    onSearch: (String) -> Unit,
    recordState: RecordState,
    onEvent: (RecordsEvent) -> Unit,
    bookState: BookState,
    bookEvent: (BookEvent) -> Unit,
) {
    recordState.trueRecord?.let {
        RecordDialog(
            trueRecord = it,
            onSaveClicked = { record ->
                onEvent(
                    RecordsEvent.DeleteRecord(
                        record
                    )
                )
            },
            onDismissDialog = {
                onEvent(
                    RecordsEvent.HideDialog
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        data.forEach {
                            val item = it.book
                            Column(
                                modifier = Modifier
                                    .width(80.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .combinedClickable(
                                        onLongClick = {
                                            bookEvent(BookEvent.ShowDialog(item))
                                        },
                                    ) {}
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
                                        id = DefaultData.savingsIcons[item.bookIconDescription]?.image
                                            ?: item.bookIcon
                                    ),
                                    contentDescription = item.bookIconDescription,
                                    tint = Color.Unspecified
                                )
                                Text(
                                    text = item.bookName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                            RecordBody(
                                recordMaps = it.recordMaps,
                                onEvent = onEvent,
                            )
                        }
                    }
                }
            }
        }

    }
}
