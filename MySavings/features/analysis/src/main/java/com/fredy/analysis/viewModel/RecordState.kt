package com.fredy.analysis.viewModel

import com.fredy.domain.model.TrueRecord
import com.fredy.theme.model.BalanceBar

data class RecordState(
    val resourceData: ResourceData = ResourceData(),
    val trueRecord: TrueRecord? = null,
    val availableCurrency: List<String> = listOf(),
    val selectedCheckbox: List<String> = listOf(),
    val balanceBar: BalanceBar = BalanceBar(),
    val isChoosingFilter: Boolean = false,
    val filterState: FilterState = FilterState(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)