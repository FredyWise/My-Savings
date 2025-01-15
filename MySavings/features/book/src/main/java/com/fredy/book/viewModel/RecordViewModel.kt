@file:OptIn(ExperimentalCoroutinesApi::class)

package com.fredy.book.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.book.domain.useCases.book.BookUseCases
import com.fredy.book.domain.useCases.record.RecordUseCases
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.modelUI.BalanceBar
import com.fredy.domain.modelUI.BalanceItem
import com.fredy.domain.modelUI.FilterState
import com.fredy.domain.modelUI.map
import com.fredy.domain.modelUI.minusDate
import com.fredy.domain.modelUI.plusDate
import com.fredy.domain.modelUI.updateDate
import com.fredy.domain.modelUI.updateType
import com.fredy.domain.repository.SyncRepository
import com.fredy.domain.util.resource.Resource
import com.fredy.ui.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordUseCases: RecordUseCases,
    private val bookUseCases: BookUseCases,
    private val syncRepository: SyncRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val FILTER_STATE_KEY = "filterState"
    }

    init {
        viewModelScope.launch {
            async {
                syncRepository.syncAll()
            }.await()
            recordUseCases.getWalletsCurrencies().collect { currency ->
                _state.update {
                    it.copy(selectedCheckbox = currency)
                }
                _filterState.updateFilterState {
                    it.copy(currencies = currency)
                }

                bookUseCases.getUserBooks().collectLatest { bookResource ->
                    when (bookResource) {
                        is Resource.Success -> {
                            _filterState.updateFilterState {
                                it.copy(currentBook = bookResource.data.first())
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

//    private val _filterState = MutableStateFlow(
//        FilterState()
//    )

    private val _filterState = savedStateHandle.getStateFlow(FILTER_STATE_KEY, syncRepository.filterSettings.value)
//    private val _filterState =  syncRepository.getFilterSettings().stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(),
//        FilterState()
//    )

    private suspend fun StateFlow<FilterState>.updateFilterState(updateFunction: (FilterState) -> FilterState) {
        savedStateHandle.update(FILTER_STATE_KEY, updateFunction)
        this.collect {
            syncRepository.saveFilterSettings(updateFunction(it))
        }
    }

    private val _availableCurrency = recordUseCases.getWalletsCurrencies().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _totalBalance = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, _, _, _, currentBook ->
            recordUseCases.getUserTotalRecordBalance(
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
            recordUseCases.getUserTotalAmountByTypeFromSpecificTime(
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
            recordUseCases.getUserTotalAmountByTypeFromSpecificTime(
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
            recordUseCases.getUserTotalAmountByTypeFromSpecificTime(
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

//    private val _categoriesWithAmount = _filterState.flatMapLatest { filterState ->
//        filterState.map { start, end, recordType, sortType, currencies, useUserCurrency, currentBook ->
//            recordUseCases.getUserCategoriesWithAmountFromSpecificTime(
//                recordType,
//                sortType,
//                start,
//                end,
//                currencies,
//                useUserCurrency,
//                currentBook
//            )
//        }
//    }.stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(),
//        Resource.Success(emptyList())
//    )
//
//    private val _accountsWithAmount = _filterState.flatMapLatest { filterState ->
//        filterState.map { start, end, _, sortType, _, useUserCurrency, currentBook ->
//            recordUseCases.getUserWalletsWithAmountFromSpecificTime(
//                sortType,
//                start,
//                end,
//                useUserCurrency,
//                currentBook,
//            )
//        }
//    }.stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(),
//        Resource.Success(emptyList())
//    )
//
//    private val _recordsWithinSpecificTime = _filterState.flatMapLatest { filterState ->
//        filterState.map { start, end, recordType, sortType, currencies, useUserCurrency, currentBook ->
//            recordUseCases.getUserRecordsFromSpecificTime(
//                recordType,
//                sortType,
//                start,
//                end,
//                currencies,
//                useUserCurrency,
//                currentBook
//            )
//        }
//    }.stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(),
//        Resource.Success(emptyList())
//    )

    private val _recordResource = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, sortType, currencies, useUserCurrency, currentBook ->
            recordUseCases.getUserTrueRecordMapsFromSpecificTime(
                start, end, sortType, currencies, useUserCurrency, currentBook
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
        _recordResource,
    ) { resourceData, recordResource ->
        resourceData.copy(

            recordMapsResource = recordResource
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ResourceData()
    )

    private val _state = MutableStateFlow(
        RecordState()
    )

    val state = combine(
        _state,
        resourceData,
        _availableCurrency,
        balanceBar,
        _filterState,
    ) { state, resourceData, availableCurrency, balanceBar, filterState ->
        state.copy(
            resourceData = resourceData,
            availableCurrency = availableCurrency,
            balanceBar = balanceBar,
            filterState = filterState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        RecordState()
    )


    fun onEvent(event: RecordEvent) {
        viewModelScope.launch {
            when (event) {
                is RecordEvent.ShowDialog -> {
                    _state.update {
                        it.copy(
                            trueRecord = event.trueRecord,
                        )
                    }
                }

                is RecordEvent.HideDialog -> {
                    _state.update {
                        it.copy(
                            trueRecord = null,
                        )
                    }
                }

                is RecordEvent.ShowFilterDialog -> {
                    _state.update {
                        it.copy(
                            isChoosingFilter = true,
                        )
                    }
                }

                is RecordEvent.HideFilterDialog -> {
                    _state.update {
                        it.copy(
                            isChoosingFilter = false,
                        )
                    }
                }

                is RecordEvent.DeleteRecord -> {
                    viewModelScope.launch {
                        recordUseCases.deleteRecordItem(
                            event.record
                        )
                    }
                }

//                is RecordEvent.ToggleRecordType -> {
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

                is RecordEvent.FilterRecord -> {
                    _filterState.updateFilterState {
                        it.updateType(event.filterType)
                    }
                }

                is RecordEvent.ShowNextList -> {
                    _filterState.updateFilterState {
                        it.plusDate()
                    }
                }

                is RecordEvent.ShowPreviousList -> {
                    _filterState.updateFilterState {
                        it.minusDate()
                    }
                }

                is RecordEvent.ChangeDate -> {
                    _filterState.updateFilterState {
                        it.updateDate(event.selectedDate)
                    }
                }

                is RecordEvent.SelectedCurrencies -> {
                    Timber.i("onEvent: ${state.value.availableCurrency}")
                    _filterState.updateFilterState {
                        it.copy(
                            currencies = event.selectedCurrencies
                        )
                    }
                    _state.update { // Assuming _state is still a MutableState
                        it.copy(selectedCheckbox = event.selectedCurrencies)
                    }
                }

                is RecordEvent.ToggleSortType -> {
                    _filterState.updateFilterState {
                        it.copy(
                            sortType = when (it.sortType) {
                                SortType.ASCENDING -> SortType.DESCENDING
                                SortType.DESCENDING -> SortType.ASCENDING
                            }
                        )
                    }
                }

                RecordEvent.ToggleCarryOn -> {
                    _filterState.updateFilterState {
                        it.copy(carryOn = !it.carryOn)
                    }
                }

                RecordEvent.ToggleShowTotal -> {
                    _filterState.updateFilterState {
                        it.copy(showTotal = !it.showTotal)
                    }
                }

                RecordEvent.ToggleUserCurrency -> {
                    _filterState.updateFilterState {
                        it.copy(useUserCurrency = !it.useUserCurrency)
                    }
                }

                RecordEvent.UpdateRecord -> {
                    _filterState.updateFilterState {
                        it.copy(updating = !it.updating)
                    }
                }

                is RecordEvent.ClickBook -> {
                    _filterState.updateFilterState {
                        it.copy(currentBook = event.book)
                    }
                }

                RecordEvent.ToggleAnalysisType -> {
                    _filterState.updateFilterState {
                        it.copy(
                            recordType = when (it.recordType) {
                                RecordType.Expense -> RecordType.Income
                                RecordType.Income -> RecordType.Transfer
                                RecordType.Transfer -> RecordType.Expense
                            }
                        )
                    }
                }
            }
        }
    }
}


