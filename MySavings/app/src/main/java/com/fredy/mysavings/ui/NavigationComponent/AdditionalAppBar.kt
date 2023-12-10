package com.fredy.mysavings.ui.NavigationComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.ui.Screens.Record.BalanceBar
import com.fredy.mysavings.ui.Screens.Record.DisplayBar
import com.fredy.mysavings.ui.Screens.Record.FilterDialog
import java.time.LocalDate

@Composable
fun AdditionalAppBar(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    selectedDateFormat: String,
    isChoosingFilter: Boolean = false,
    selectedFilter: String,
    onDismissFilterDialog: () -> Unit,
    onSelectFilter: (FilterType) -> Unit,
    totalExpense: Double,
    totalIncome: Double,
    totalBalance: Double,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    if (isChoosingFilter) {
        FilterDialog(
            title = "DisplayOption",
            selectedName = selectedFilter,
            onDismissRequest = onDismissFilterDialog,
            onSelectItem = { item->
                onSelectFilter(item)
            },
        )
    }
    Column (modifier = modifier){
        DisplayBar(
            selectedDate = selectedDate,
            onDateChange = {
                onDateChange(it)
            },
            selectedTitle = selectedDateFormat,
            onPrevious = onPrevious,
            onNext = onNext,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
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
                    amount = totalExpense
                ),
                BalanceItem(
                    name = "INCOME",
                    amount = totalIncome
                ),
                BalanceItem(
                    name = "BALANCE",
                    amount = totalBalance
                ),
            )
        )

        content()
    }
}