@file:OptIn(ExperimentalCoroutinesApi::class)

package com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Repository.SyncRepository
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.BookUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.WalletUseCases
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Util.BalanceBar
import com.fredy.mysavings.Feature.Presentation.Util.BalanceItem
import com.fredy.mysavings.Feature.Presentation.Util.update
import com.fredy.mysavings.Util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordUseCases: RecordUseCases,
    private val walletUseCases: WalletUseCases,
    private val bookUseCases: BookUseCases,
    private val syncRepository: SyncRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object{
        const val FILTER_STATE_KEY = "filterState"
    }
    init {
        viewModelScope.launch {
            async {
                syncRepository.syncAll()
            }.await()
            walletUseCases.getWalletsCurrencies().collect { currency ->
                _state.update {
                    it.copy(selectedCheckbox = currency)
                }
                savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                    it.copy(currencies = currency)
                }

                bookUseCases.getUserBooks().collectLatest { bookResource ->
                    when (bookResource) {
                        is Resource.Success -> {
                            savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                                it.copy(currentBook = bookResource.data!!.first())
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

    private val _filterState = savedStateHandle.getStateFlow(FILTER_STATE_KEY, FilterState())

    private val _availableCurrency = walletUseCases.getWalletsCurrencies().stateIn(
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

    private val _categoriesWithAmount = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, recordType, sortType, currencies, useUserCurrency, currentBook ->
            recordUseCases.getUserCategoriesWithAmountFromSpecificTime(
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
            recordUseCases.getUserWalletsWithAmountFromSpecificTime(
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
            recordUseCases.getUserRecordsFromSpecificTime(
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
        _categoriesWithAmount,
        _recordsWithinSpecificTime,
        _accountsWithAmount,
        _recordResource,
    ) { resourceData, categoriesWithAmount, recordsWithinSpecificTime, accountsWithAmount, recordResource ->
        resourceData.copy(
            categoriesWithAmountResource = categoriesWithAmount,
            accountsWithAmountResource = accountsWithAmount,
            recordsWithinTimeResource = recordsWithinSpecificTime,
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

                is RecordEvent.ToggleRecordType -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.copy(
                            recordType = when (it.recordType) {
                                RecordType.Expense -> RecordType.Income
                                RecordType.Income -> RecordType.Transfer
                                RecordType.Transfer -> RecordType.Expense
                            }
                        )
                    }
                }

                is RecordEvent.FilterRecord -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.updateType(event.filterType)
                    }
                }

                is RecordEvent.ShowNextList -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.plusDate()
                    }
                }

                is RecordEvent.ShowPreviousList -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.minusDate()
                    }
                }

                is RecordEvent.ChangeDate -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.updateDate(event.selectedDate)
                    }
                }

                is RecordEvent.SelectedCurrencies -> {
                    Log.i("onEvent: ${state.value.availableCurrency}")
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.copy(
                            currencies = event.selectedCurrencies
                        )
                    }
                    _state.update { // Assuming _state is still a MutableState
                        it.copy(selectedCheckbox = event.selectedCurrencies)
                    }
                }

                is RecordEvent.ToggleSortType -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.copy(
                            sortType = when (it.sortType) {
                                SortType.ASCENDING -> SortType.DESCENDING
                                SortType.DESCENDING -> SortType.ASCENDING
                            }
                        )
                    }
                }

                RecordEvent.ToggleCarryOn -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.copy(carryOn = !it.carryOn)
                    }
                }

                RecordEvent.ToggleShowTotal -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.copy(showTotal = !it.showTotal)
                    }
                }

                RecordEvent.ToggleUserCurrency -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.copy(useUserCurrency = !it.useUserCurrency)
                    }
                }

                RecordEvent.UpdateRecord -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.copy(updating = !it.updating)
                    }
                }

                is RecordEvent.ClickBook -> {
                    savedStateHandle.update<FilterState>(FILTER_STATE_KEY) {
                        it.copy(currentBook = event.book)
                    }
                }
            }
        }
    }
}


