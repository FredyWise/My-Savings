package com.fredy.mysavings.ui.Screens.Record

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fredy.mysavings.Data.BalanceItem
import com.fredy.mysavings.Data.RoomDatabase.Enum.FilterType
import com.fredy.mysavings.Data.formatDate
import com.fredy.mysavings.Data.formatDateTime
import com.fredy.mysavings.Data.formatRangeOfDate
import com.fredy.mysavings.ViewModel.FilterState
import com.fredy.mysavings.ViewModel.RecordViewModel
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.NavigationComponent.Navigation.navigateSingleTopTo
import com.fredy.mysavings.ui.Screens.ValueWithName
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    viewModel: RecordViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = modifier,
    ) {
        if (state.isChoosingFilter) {
            FilterDialog(
                title = "DisplayOption",
                selectedName = state.filterType.name,
                onEvent = viewModel::onEvent,
            )
        }
        state.trueRecord?.let {
            RecordDialog(trueRecord = it,
                onEvent = viewModel::onEvent,
                onEdit = {
                    rootNavController.navigateSingleTopTo(
                        NavigationRoute.Add.route + "?id=" + it.record.recordId
                    )
                })
        }
        DisplayBar(
            onEvent = viewModel::onEvent,
            selectedDate = formatRangeOfDate(state.chosenDate,state.filterType)
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
            onEvent = viewModel::onEvent
        )
    }
}

