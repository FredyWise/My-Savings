package com.fredy.mysavings.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.GraphType
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.AccountWithAmountType
import com.fredy.mysavings.Data.Repository.CategoryWithAmount
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Util.BalanceBar
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.FilterState
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.map
import com.fredy.mysavings.Util.minusDate
import com.fredy.mysavings.Util.plusDate
import com.fredy.mysavings.Util.updateDate
import com.fredy.mysavings.Util.updateType
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val accountRepository: AccountRepository,
): ViewModel() {

    init {
        viewModelScope.launch {
            accountRepository.getUserAvailableCurrency().collectLatest { currency ->
                _state.update {
                    it.copy(selectedCheckbox = currency)
                }
                _filterState.update {
                    it.copy(currencies = currency)
                }
            }
            _filterState.update {
                it.copy(updating = !it.updating)
            }
        }
    }

    private val _filterState = MutableStateFlow(
        FilterState()
    )

    private val _availableCurrency = accountRepository.getUserAvailableCurrency().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )


    private val _categoriesWithAmount = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, recordType, sortType, currencies ->
            recordRepository.getUserCategoriesWithAmountFromSpecificTime(
                recordType,
                sortType,
                start,
                end,
                currencies,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _accountsWithAmount = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, sortType, _ ->
            recordRepository.getUserAccountsWithAmountFromSpecificTime(
                sortType,
                start,
                end,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _recordsWithinSpecificTime = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, recordType, sortType, currencies ->
            recordRepository.getUserRecordsFromSpecificTime(
                recordType,
                sortType,
                start,
                end,
                currencies,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _recordResource = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, sortType, currencies ->
            recordRepository.getUserTrueRecordMapsFromSpecificTime(
                start, end, sortType, currencies
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _totalBalance = _filterState.flatMapLatest {recordRepository.getUserTotalRecordBalance()}.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalExpense = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, _, _ ->
            recordRepository.getUserTotalAmountByTypeFromSpecificTime(
                RecordType.Expense, start, end
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalIncome = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, _, _ ->
            recordRepository.getUserTotalAmountByTypeFromSpecificTime(
                RecordType.Income, start, end
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
        _filterState,
    ) { balanceBar, totalExpense, totalIncome, totalBalance, filterState ->
        balanceBar.copy(
            expense = totalExpense,
            income = totalIncome,
            balance = if (filterState.carryOn) totalBalance else BalanceItem(
                name = totalBalance.name,
                amount = totalExpense.amount + totalIncome.amount,
                currency = totalBalance.currency
            ),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceBar()
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
    ) { resourceData, categoriesWithAmount, recordsWithinSpecificTime, accountsWithAmount,recordResource ->
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
        SharingStarted.WhileSubscribed( 5000),
        RecordState()
    )


    fun onEvent(event: RecordsEvent) {
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
                        recordType = when(it.recordType){
                            RecordType.Expense -> RecordType.Income
                            RecordType.Income -> RecordType.Expense
                            RecordType.Transfer -> RecordType.Expense
                        }
                    )
                }
            }

            is RecordsEvent.DeleteRecord -> {
                viewModelScope.launch {
                    recordRepository.deleteRecordItem(
                        event.record
                    )
                    onEvent(RecordsEvent.UpdateRecord)
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
                    TAG,
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
                _filterState.update {
                    it.copy(carryOn = !it.carryOn)
                }
            }

            RecordsEvent.ToggleShowTotal -> {
                _filterState.update {
                    it.copy(showTotal = !it.showTotal)
                }
            }

            RecordsEvent.UpdateRecord -> {
                _filterState.update {
                    it.copy(updating = !it.updating)
                }
            }
        }
    }

}

data class RecordState(
    val resourceData: ResourceData = ResourceData(),
    val graphType: GraphType = GraphType.SlimDonut,
    val trueRecord: TrueRecord? = null,
    val availableCurrency: List<String> = listOf(),
    val selectedCheckbox: List<String> = listOf(),
    val balanceBar: BalanceBar = BalanceBar(),
    val isChoosingFilter: Boolean = false,
    val filterState: FilterState = FilterState(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)

data class RecordMap(
    val recordDate: LocalDate,
    val records: List<TrueRecord>
)

data class ResourceData(
    val categoriesWithAmountResource: Resource<List<CategoryWithAmount>> = Resource.Loading(),
    val accountsWithAmountResource: Resource<List<AccountWithAmountType>> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>> = Resource.Loading(),
    val recordMapsResource: Resource<List<RecordMap>> = Resource.Loading(),
)



