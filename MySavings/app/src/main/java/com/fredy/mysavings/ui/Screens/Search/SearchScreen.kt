package com.fredy.mysavings.ui.Search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ViewModels.SearchState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.Record.RecordBody
import com.fredy.mysavings.ui.Screens.Record.RecordDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem

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
            onEvent = onEvent,
            onEdit = {
                rootNavController.navigate(
                    NavigationRoute.Add.route + "/" + it.record.recordId
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
                        nullOrEmptyMessage = "There is no record on this date yet",
                        isNullOrEmpty = { it.isNullOrEmpty() },
                        errorMessage = resource.message ?: "",
                        onMessageClick = {
                            rootNavController.navigate(
                                NavigationRoute.Add.route + "/-1"
                            )
//                        isVisible.targetState = false
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
