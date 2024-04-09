package com.fredy.mysavings.ui.Search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ViewModels.SearchState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.Record.RecordBody
import com.fredy.mysavings.ui.Screens.Record.RecordDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SearchBar

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavController,
    title: String,
    state: SearchState,
    onSearch: (String) -> Unit,
    recordState: RecordState,
    onEvent: (RecordsEvent) -> Unit
) {
    val key = state.trueRecordsResource.hashCode()
    val isVisible = remember(key) {
        MutableTransitionState(
            false
        ).apply { targetState = true }
    }
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
                    "${NavigationRoute.Add.route}?recordId=${it.record.recordId}"
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
            AnimatedVisibility(
                modifier = modifier,
                visibleState = isVisible,
                enter = slideInVertically(
                    animationSpec = tween(
                        durationMillis = 500
                    ),
                    initialOffsetY = { fullHeight -> fullHeight },
                ) + fadeIn(),
                exit = slideOutVertically(
                    animationSpec = tween(
                        durationMillis = 500
                    ),
                    targetOffsetY = { fullHeight -> fullHeight },
                ) + fadeOut()
            ) {
                state.trueRecordsResource.let { resource ->
                    ResourceHandler(
                        resource = resource,
                        nullOrEmptyMessage = "You didn't have any records yet",
                        isNullOrEmpty = { it.isNullOrEmpty() },
                        errorMessage = resource.message ?: "",
                        onMessageClick = {
                            rootNavController.navigate(
                                NavigationRoute.Add.route
                            )
                        },
                    ) { data ->
                        RecordBody(
                            trueRecordMaps = data,
                            onEvent = onEvent
                        )
                    }
                }
            }
        }
    }
}
