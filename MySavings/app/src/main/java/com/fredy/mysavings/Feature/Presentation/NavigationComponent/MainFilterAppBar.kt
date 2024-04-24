package com.fredy.mysavings.Feature.Presentation.NavigationComponent

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Data.Enum.FilterType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Presentation.Util.BalanceBar
import com.fredy.mysavings.Feature.Presentation.Screens.Record.BalanceBar
import com.fredy.mysavings.Feature.Presentation.Screens.Record.DisplayBar
import com.fredy.mysavings.Feature.Presentation.Screens.Record.FilterDialog
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
    Column(
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    stiffness = Spring.StiffnessLow,
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            )
            .heightIn(max = if (onShowAppBar) 100.dp else 0.dp)
    ) {
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
}
