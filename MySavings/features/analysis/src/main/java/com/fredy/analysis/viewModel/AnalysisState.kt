package com.fredy.analysis.viewModel

import com.fredy.domain.modelUI.FilterState
import com.fredy.domain.modelUI.BalanceBar

data class AnalysisState(
    val resourceData: ResourceData = ResourceData(),
    val balanceBar: BalanceBar = BalanceBar(),
    val filterState: FilterState = FilterState(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)