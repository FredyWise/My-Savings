package com.fredy.mysavings.ui.Screens.Record

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.NavigationComponent.MainFilterAppBar
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
) {
    val key = state.resourceData.recordMapsResource.hashCode()
    val isVisible = remember(key) {
        MutableTransitionState(
            false
        ).apply { targetState = true }
    }
    state.trueRecord?.let {
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
                    NavigationRoute.Add.route + "/" + it.record.recordId
                )
            },
        )
    }
    MainFilterAppBar(
        modifier = modifier,
        selectedDate = state.filterState.selectedDate,
        onDateChange = {
            onEvent(
                RecordsEvent.ChangeDate(it)
            )
        },
        selectedDateFormat = formatRangeOfDate(
            state.filterState.selectedDate,
            state.filterState.filterType
        ),
        isChoosingFilter = state.isChoosingFilter,
        selectedFilter = state.filterState.filterType.name,
        checkboxesFilter = state.availableCurrency,
        selectedCheckbox = state.selectedCheckbox,
        onDismissFilterDialog = {
            onEvent(RecordsEvent.HideFilterDialog)
        },
        onSelectFilter = {
            onEvent(
                RecordsEvent.FilterRecord(it)
            )
        },
        onSelectCheckboxFilter = {
            onEvent(
                RecordsEvent.SelectedCurrencies(it)
            )
        },
        balanceBar = state.balanceBar,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        sortType = state.filterState.sortType,
        showTotal = state.filterState.showTotal,
        carryOn = state.filterState.carryOn,
        onShowTotalChange = { onEvent(RecordsEvent.ToggleShowTotal) },
        onCarryOnChange = { onEvent(RecordsEvent.ToggleCarryOn) },
        onShortChange = { onEvent(RecordsEvent.ToggleSortType) },
        onPrevious = { onEvent(RecordsEvent.ShowPreviousList) },
        onNext = { onEvent(RecordsEvent.ShowNextList) },
        onLeadingIconClick = {
            rootNavController.navigate(
                NavigationRoute.Search.route
            )
        },
        onTrailingIconClick = {
            onEvent(RecordsEvent.ShowFilterDialog)
        },
    ) {
        AnimatedVisibility(
            modifier = modifier,
            visibleState = isVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
            ) + fadeOut()
        ) {
            state.resourceData.recordMapsResource.let { resource ->
                ResourceHandler(
                    resource = resource,
                    nullOrEmptyMessage = "There is no record on this date yet",
                    isNullOrEmpty = { it.isNullOrEmpty() },
                    errorMessage = resource.message ?: "",
                    onMessageClick = {
                        rootNavController.navigate(
                            NavigationRoute.Add.route + "/-1"
                        )
                        isVisible.targetState = false
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

