package com.fredy.mysavings.ui.Screens.Record

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.ViewModel.FilterState
import com.fredy.mysavings.ViewModel.RecordState
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ui.NavigationComponent.AdditionalAppBar
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.ZCommonComponent.LoadingAnimation


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
    resource: ResourceState,
) {
    val key = state.trueRecordMaps.hashCode()
    val isVisible = remember(key) {
        MutableTransitionState(
            false
        ).apply { targetState = true }
    }
    state.trueRecord?.let {
        RecordDialog(
            trueRecord = it,
            onEvent = onEvent,
            onEdit = {
                rootNavController.navigate(
                    NavigationRoute.Add.route + "?id=" + it.record.recordId
                )
            },
        )
    }
    AdditionalAppBar(
        modifier = modifier,
        selectedDate = state.selectedDate,
        onDateChange = {
            onEvent(
                RecordsEvent.ChangeDate(it)
            )
        },
        selectedDateFormat = formatRangeOfDate(
            state.selectedDate, state.filterType
        ),
        isChoosingFilter = state.isChoosingFilter,
        selectedFilter = state.filterType.name,
        checkboxesFilter = state.availableCurrency,
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
        totalExpense = state.totalExpense,
        totalIncome = state.totalIncome,
        totalBalance = state.totalAll,
        leadingIcon = {
            Icon(
                modifier = Modifier
                    .clip(
                        MaterialTheme.shapes.extraLarge
                    )
                    .clickable {

                    }
                    .padding(4.dp),
                imageVector = Icons.Outlined.Info,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .clip(
                        MaterialTheme.shapes.extraLarge
                    )
                    .clickable {
                        onEvent(RecordsEvent.ShowFilterDialog)
                    }
                    .padding(4.dp),
                imageVector = Icons.Default.FilterList,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        onPrevious = { onEvent(RecordsEvent.ShowPreviousList) },
        onNext = { onEvent(RecordsEvent.ShowNextList) },
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

            state.trueRecordMaps.let { records ->
                if (records.isNotEmpty() && records.first().records.first().record.recordCurrency.isNotEmpty()) {
                    RecordBody(
                        trueRecords = state.trueRecordMaps,
                        onEvent = onEvent
                    )
                } else {
                    LoadingAnimation(
                        isLoading = resource.isLoading && records.isNotEmpty(),
                        notLoadingMessage = "You haven't made any Record yet",
                        onClick = {
                            rootNavController.navigate(
                                NavigationRoute.Add.route + "?id=-1"
                            )
                            isVisible.targetState = false
                        },
                    )
                }
            }
        }
    }
}

