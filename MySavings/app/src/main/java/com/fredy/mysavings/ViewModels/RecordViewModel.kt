@file:OptIn(ExperimentalCoroutinesApi::class)

package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Data.Database.Model.BookMap
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Repository.AccountWithAmountType
import com.fredy.mysavings.Feature.Domain.Repository.CategoryWithAmount
import com.fredy.mysavings.Feature.Domain.Repository.SettingsRepository
import com.fredy.mysavings.Feature.Domain.Repository.SyncRepository
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.WalletUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.BookUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Util.BalanceBar
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.FilterState
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.map
import com.fredy.mysavings.Util.minusDate
import com.fredy.mysavings.Util.plusDate
import com.fredy.mysavings.Util.updateDate
import com.fredy.mysavings.Util.updateType
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
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
    private val settingsRepository: SettingsRepository,
    private val syncRepository: SyncRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
//            settingsRepository.getAllPreferenceView().collectLatest{ filterState->
//                filterState?.let{
//                    _filterState.update {
//                        filterState
//                    }
//                }
//            }
            async {
                syncRepository.syncAll()
            }.await()
            walletUseCases.getWalletsCurrencies().collect { currency ->
                _state.update {
                    it.copy(selectedCheckbox = currency)
                }
                _filterState.update {
                    it.copy(currencies = currency)
                }
                bookUseCases.getUserBooks().collectLatest { bookResource ->
                    when (bookResource) {
                        is Resource.Success -> {
                            _filterState.update {
                                it.copy(currentBook = bookResource.data!!.first())
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private val _filterState = MutableStateFlow(
        FilterState()
    )

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
            recordUseCases.getUserAccountsWithAmountFromSpecificTime(
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


    fun onEvent(event: RecordsEvent) {
        viewModelScope.launch {
            when (event) {
                is RecordsEvent.ShowDialog -> {
                    _state.update {
                        it.copy(
                            trueRecord = event.trueRecord,
                        )
                    }
                }

                is RecordsEvent.HideDialog -> {
                    _state.update {
                        it.copy(
                            trueRecord = null,
                        )
                    }
                }

                is RecordsEvent.ShowFilterDialog -> {
                    _state.update {
                        it.copy(
                            isChoosingFilter = true,
                        )
                    }
                }

                is RecordsEvent.HideFilterDialog -> {
                    _state.update {
                        it.copy(
                            isChoosingFilter = false,
                        )
                    }
                }

                is RecordsEvent.ToggleRecordType -> {
                    _filterState.update {
                        it.copy(
                            recordType = when (it.recordType) {
                                RecordType.Expense -> RecordType.Income
                                RecordType.Income -> RecordType.Transfer
                                RecordType.Transfer -> RecordType.Expense
                            }
                        )
                    }

                }

                is RecordsEvent.DeleteRecord -> {
                    viewModelScope.launch {
                        recordUseCases.deleteRecordItem(
                            event.record
                        )
                    }
                }

                is RecordsEvent.FilterRecord -> {
                    _filterState.update {
                        it.updateType(event.filterType)
                    }
                }

                is RecordsEvent.ShowNextList -> {
                    _filterState.update {
                        it.plusDate()
                    }
                }

                is RecordsEvent.ShowPreviousList -> {
                    _filterState.update {
                        it.minusDate()
                    }
                }

                is RecordsEvent.ChangeDate -> {
                    _filterState.update {
                        it.updateDate(event.selectedDate)
                    }
                }

                is RecordsEvent.SelectedCurrencies -> {
                    Log.i(
                        "onEvent: ${state.value.availableCurrency}"
                    )
                    _filterState.update {
                        it.copy(
                            currencies = event.selectedCurrencies
                        )
                    }
                    _state.update {
                        it.copy(selectedCheckbox = event.selectedCurrencies)
                    }
                }

                is RecordsEvent.ToggleSortType -> {
                    _filterState.update {
                        it.copy(
                            sortType = when (it.sortType) {
                                SortType.ASCENDING -> SortType.DESCENDING
                                SortType.DESCENDING -> SortType.ASCENDING
                            }
                        )
                    }
                }

                RecordsEvent.ToggleCarryOn -> {
                    viewModelScope.launch {
                        settingsRepository.saveCarryOn(!_filterState.value.carryOn)
                    }
                    _filterState.update {
                        it.copy(carryOn = !it.carryOn)
                    }
                }

                RecordsEvent.ToggleShowTotal -> {
                    viewModelScope.launch {
                        settingsRepository.saveShowTotal(!_filterState.value.showTotal)
                    }
                    _filterState.update {
                        it.copy(showTotal = !it.showTotal)
                    }
                }

                RecordsEvent.ToggleUserCurrency -> {
                    _filterState.update {
                        it.copy(useUserCurrency = !it.useUserCurrency)
                    }
                }

                RecordsEvent.UpdateRecord -> {
                    _filterState.update {
                        it.copy(updating = !it.updating)
                    }
                }

                is RecordsEvent.ClickBook -> {
                    _filterState.update {
                        it.copy(currentBook = event.book)
                    }
                }
            }
        }
    }
}

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

data class ResourceData(
    val categoriesWithAmountResource: Resource<List<CategoryWithAmount>> = Resource.Loading(),
    val accountsWithAmountResource: Resource<List<AccountWithAmountType>> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>> = Resource.Loading(),
    val recordMapsResource: Resource<List<BookMap>> = Resource.Loading(),
)



