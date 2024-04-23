package com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel

import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Util.BalanceBar
import com.fredy.mysavings.Util.FilterState

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