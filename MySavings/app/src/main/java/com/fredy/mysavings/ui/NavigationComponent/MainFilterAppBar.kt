package com.fredy.mysavings.ui.NavigationComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Util.BalanceBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.BalanceBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DisplayBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.FilterDialog
import java.time.LocalDate

@Composable
fun MainFilterAppBar(
    modifier: Modifier = Modifier,
    onShowAppBar: Boolean = true,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    selectedDateFormat: String,
    isChoosingFilter: Boolean = false,
    selectedFilter: String,
    onSelectFilter: (FilterType) -> Unit,
    checkboxesFilter: List<String> = emptyList(),
    selectedCheckbox: List<String> = emptyList(),
    onSelectCheckboxFilter: (List<String>) -> Unit,
    sortType: SortType = SortType.DESCENDING,
    carryOn: Boolean = true,
    showTotal: Boolean = true,
    useUserCurrency: Boolean = true,
    onShortChange: () -> Unit = {},
    onCarryOnChange: () -> Unit = {},
    onShowTotalChange: () -> Unit = {},
    onUserCurrencyChange: () -> Unit = {},
    onDismissFilterDialog: () -> Unit,
    balanceBar: BalanceBar,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onLeadingIconClick: () -> Unit,
    onTrailingIconClick: () -> Unit,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val displayedBalance = mutableListOf(
        balanceBar.expense,
        balanceBar.income,
    )
    if (showTotal) {
        displayedBalance.add(balanceBar.balance)
    }

    if (isChoosingFilter) {
        FilterDialog(
            title = "Display Option",
            selectedName = selectedFilter,
            checkboxList = checkboxesFilter,
            selectedCheckbox = selectedCheckbox,
            sortType = sortType,
            carryOn = carryOn,
            showTotal = showTotal,
            useUserCurrency = useUserCurrency,
            onShortChange = onShortChange,
            onCarryOnChange = onCarryOnChange,
            onShowTotalChange = onShowTotalChange,
            onUserCurrencyChange = onUserCurrencyChange,
            onDismissRequest = onDismissFilterDialog,
            onSelectItem = { item ->
                onSelectFilter(item)
            },
            onSelectCheckbox = { item ->
                onSelectCheckboxFilter(item)
            },
        )
    }
    Column(modifier = modifier) {
        if (onShowAppBar) {
            DisplayBar(
                selectedDate = selectedDate,
                onDateChange = {
                    onDateChange(it)
                },
                selectedTitle = selectedDateFormat,
                onPrevious = onPrevious,
                onNext = onNext,
                onLeadingIconClick = onLeadingIconClick,
                onTrailingIconClick = onTrailingIconClick,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
            )
            BalanceBar(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface
                    )
                    .padding(vertical = 5.dp),
                amountBars = displayedBalance
            )
        }
        content()
    }
}
