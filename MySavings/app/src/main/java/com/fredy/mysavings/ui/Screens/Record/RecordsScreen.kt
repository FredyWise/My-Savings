package com.fredy.mysavings.ui.Screens.Record

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.ViewModel.RecordState
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        if (state.isChoosingFilter) {
            FilterDialog(
                title = "DisplayOption",
                selectedName = state.filterType.name,
                onDismissRequest = {
                    onEvent(
                        RecordsEvent.HideFilterDialog
                    )
                },
                onEvent = {
                    onEvent(
                        RecordsEvent.FilterRecord(
                            it
                        )
                    )
                },
            )
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
        DisplayBar(
            selectedData = formatRangeOfDate(
                state.chosenDate, state.filterType
            ),
            onPrevious = { onEvent(RecordsEvent.ShowPreviousList) },
            onNext = { onEvent(RecordsEvent.ShowNextList) },
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
        )
        BalanceBar(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface
                )
                .padding(vertical = 5.dp),
            amountBars = listOf(
                BalanceItem(
                    name = "EXPENSE",
                    amount = state.totalExpense
                ),
                BalanceItem(
                    name = "INCOME",
                    amount = state.totalIncome
                ),
                BalanceItem(
                    name = "BALANCE",
                    amount = state.totalAll
                ),
            )
        )
        RecordBody(
            trueRecords = state.trueRecordMaps,
            onEvent = onEvent
        )
    }
}

