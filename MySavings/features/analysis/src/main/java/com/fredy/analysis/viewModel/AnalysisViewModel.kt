@file:OptIn(ExperimentalCoroutinesApi::class)

package com.fredy.analysis.viewModel


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.analysis.domain.useCases.AnalysisUseCases
import com.fredy.domain.enums.RecordType
import com.fredy.domain.modelUI.BalanceBar
import com.fredy.domain.modelUI.BalanceItem
import com.fredy.domain.modelUI.FilterState
import com.fredy.domain.modelUI.map
import com.fredy.domain.repository.SyncRepository
import com.fredy.domain.util.resource.Resource
import com.fredy.ui.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analysisUseCases: AnalysisUseCases,
    private val syncRepository: SyncRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val FILTER_STATE_KEY = "filterState"
    }


//    private val _filterState = savedStateHandle.getStateFlow(FILTER_STATE_KEY, FilterState())
    private val _filterState = syncRepository.filterSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        FilterState()
    )

//    private suspend fun StateFlow<FilterState>.updateFilterState(updateFunction: (FilterState) -> FilterState) {
//        this.collectLatest {
//            savedStateHandle.update(FILTER_STATE_KEY, updateFunction)
//            syncRepository.saveFilterSettings(updateFunction(it))
//        }
//    }


    private val _totalBalance = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, _, _, _, currentBook ->
            analysisUseCases.getUserTotalRecordBalance(
                filterState.carryOn,
                start,
                end,
                currentBook
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalExpense = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, _, _, _, currentBook ->
            analysisUseCases.getUserTotalAmountByTypeFromSpecificTime(
                RecordType.Expense, start, end, currentBook
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalIncome = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, _, _, _, currentBook ->
            analysisUseCases.getUserTotalAmountByTypeFromSpecificTime(
                RecordType.Income, start, end, currentBook
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalTransfer = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, _, _, _, currentBook ->
            analysisUseCases.getUserTotalAmountByTypeFromSpecificTime(
                RecordType.Transfer, start, end, currentBook
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _balanceBar = MutableStateFlow(
        BalanceBar()
    )

    private val balanceBar = combine(
        _balanceBar,
        _totalExpense,
        _totalIncome,
        _totalBalance,
        _totalTransfer,
    ) { balanceBar, totalExpense, totalIncome, totalBalance, totalTransfer ->
        balanceBar.copy(
            expense = totalExpense,
            income = totalIncome,
            balance = totalBalance,
            transfer = totalTransfer,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceBar()
    )

    private val _categoriesWithAmount = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, recordType, sortType, currencies, useUserCurrency, currentBook ->
            analysisUseCases.getUserCategoriesWithAmountFromSpecificTime(
                recordType,
                sortType,
                start,
                end,
                currencies,
                useUserCurrency,
                currentBook
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _accountsWithAmount = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, sortType, _, useUserCurrency, currentBook ->
            analysisUseCases.getUserWalletsWithAmountFromSpecificTime(
                sortType,
                start,
                end,
                useUserCurrency,
                currentBook,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _recordsWithinSpecificTime = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, recordType, sortType, currencies, useUserCurrency, currentBook ->
            analysisUseCases.getUserRecordsFromSpecificTime(
                recordType,
                sortType,
                start,
                end,
                currencies,
                useUserCurrency,
                currentBook
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _resourceData = MutableStateFlow(
        ResourceData()
    )

    private val resourceData = combine(
        _resourceData,
        _categoriesWithAmount,
        _recordsWithinSpecificTime,
        _accountsWithAmount,
    ) { resourceData, categoriesWithAmount, recordsWithinSpecificTime, accountsWithAmount ->
        resourceData.copy(
            categoriesWithAmountResource = categoriesWithAmount,
            walletsWithAmountResource = accountsWithAmount,
            recordsWithinTimeResource = recordsWithinSpecificTime,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ResourceData()
    )

    private val _state = MutableStateFlow(
        AnalysisState()
    )

    val state = combine(
        _state,
        resourceData,
//        _availableCurrency,
        balanceBar,
        _filterState,
    ) { state, resourceData,/* availableCurrency, */balanceBar, filterState ->
        state.copy(
            resourceData = resourceData,
//            availableCurrency = availableCurrency,
            balanceBar = balanceBar,
            filterState = filterState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AnalysisState()
    )


//    fun onEvent(event: AnalysisEvent) {
//        viewModelScope.launch {
//            when (event) {
//
//                is AnalysisEvent.DeleteAnalysis -> {
//                    viewModelScope.launch {
//                        analysisUseCases.deleteRecordItem(
//                            event.record
//                        )
//                    }
//                }
//
//                AnalysisEvent.UpdateAnalysis -> {
//                    _filterState.updateFilterState {
//                        it.copy(updating = !it.updating)
//                    }
//                }
//
//                is AnalysisEvent.ToggleAnalysisType -> {
//                    _filterState.updateFilterState {
//                        it.copy(
//                            recordType = when (it.recordType) {
//                                RecordType.Expense -> RecordType.Income
//                                RecordType.Income -> RecordType.Transfer
//                                RecordType.Transfer -> RecordType.Expense
//                            }
//                        )
//                    }
//                }
//
//            }
//        }
//    }
}


