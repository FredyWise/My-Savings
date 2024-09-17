package com.fredy.analysis.viewModel

import com.fredy.ui.FilterState
import com.fredy.domain.modelUI.BalanceBar

data class AnalysisState(
    val resourceData: ResourceData = ResourceData(),
//    val trueRecord: TrueRecord? = null,
//    val availableCurrency: List<String> = listOf(),
//    val selectedCheckbox: List<String> = listOf(),
    val balanceBar: BalanceBar = BalanceBar(),
//    val isChoosingFilter: Boolean = false,
    val filterState: FilterState = FilterState(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)