package com.fredy.mysavings.ui.Screens.Record

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.ViewModel.RecordState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.NavigationComponent.Navigation.navigateSingleTopTo


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
                onEvent = onEvent,
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
            onEvent = onEvent,
            selectedDate = formatRangeOfDate(
                state.chosenDate,
                state.filterType
            )
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
            trueRecords = state.trueRecords,
            onEvent = onEvent
        )
    }
}

