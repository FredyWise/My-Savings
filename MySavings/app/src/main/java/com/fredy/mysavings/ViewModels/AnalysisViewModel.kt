package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Enum.FilterType
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
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.map
import com.fredy.mysavings.Util.minusDate
import com.fredy.mysavings.Util.plusDate
import com.fredy.mysavings.Util.updateDate
import com.fredy.mysavings.Util.updateType
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
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
class AnalysisViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val accountRepository: AccountRepository,
): ViewModel() {
    init {
        viewModelScope.launch {
            accountRepository.getUserAvailableCurrency().collectLatest { currency ->
                _filterState.update {
                    it.copy(currencies = currency)
                }
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


    private val _totalBalance = recordRepository.getUserTotalRecordBalance().stateIn(
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
        SharingStarted.WhileSubscribed(5000),
        BalanceBar()
    )

    private val _analysisData = MutableStateFlow(
        AnalysisData()
    )

    private val analysisData = combine(
        _analysisData,
        _categoriesWithAmount,
        _recordsWithinSpecificTime,
        _accountsWithAmount,
    ) { analysisData, categoriesWithAmount, recordsWithinSpecificTime, accountsWithAmount ->
        analysisData.copy(
            categoriesWithAmountResource = categoriesWithAmount,
            accountsWithAmountResource = accountsWithAmount,
            recordsWithinTimeResource = recordsWithinSpecificTime,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AnalysisData()
    )

    private val _state = MutableStateFlow(
        AnalysisState()
    )

    val state = combine(
        _state,
        balanceBar,
        analysisData,
        _availableCurrency,
        _filterState,
    ) { state, balanceBar, analysisData, availableCurrency, filterState ->
        state.copy(
            analysisData = analysisData,
            availableCurrency = availableCurrency,
            balanceBar = balanceBar,
            filterState = filterState,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AnalysisState()
    )


    fun onEvent(event: AnalysisEvent) {
        when (event) {
            is AnalysisEvent.ShowFilterDialog -> {
                _state.update {
                    it.copy(
                        isChoosingFilter = true,
                    )
                }
            }

            is AnalysisEvent.HideFilterDialog -> {
                _state.update {
                    it.copy(
                        isChoosingFilter = false,
                    )
                }
            }

            is AnalysisEvent.DeleteRecord -> {
                viewModelScope.launch {
                    recordRepository.deleteRecordItem(
                        event.record
                    )
                }
            }

            is AnalysisEvent.FilterRecord -> {
                _filterState.update {
                    it.updateType(event.filterType)
                }
            }

            is AnalysisEvent.ShowNextList -> {
                _filterState.update {
                    it.plusDate()
                }
            }

            is AnalysisEvent.ShowPreviousList -> {
                _filterState.update {
                    it.minusDate()
                }
            }

            is AnalysisEvent.ChangeDate -> {
                _filterState.update {
                    it.updateDate(event.selectedDate)
                }
            }

            is AnalysisEvent.ToggleRecordType -> {
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

            is AnalysisEvent.SelectedCurrencies -> {
                _filterState.update {
                    it.copy(
                        currencies = event.selectedCurrencies
                    )
                }
                _state.update {
                    it.copy(selectedCheckbox = event.selectedCurrencies)
                }
            }

            is AnalysisEvent.ToggleSortType -> {
                _filterState.update {
                    it.copy(
                        sortType = when (it.sortType) {
                            SortType.ASCENDING -> SortType.DESCENDING
                            SortType.DESCENDING -> SortType.ASCENDING
                        }
                    )
                }
            }

            AnalysisEvent.ToggleCarryOn -> {
                _filterState.update {
                    it.copy(carryOn = !it.carryOn)
                }
            }
            AnalysisEvent.ToggleShowTotal -> {
                _filterState.update {
                    it.copy(showTotal = !it.showTotal)
                }
            }
        }
    }

}

data class AnalysisState(
    val analysisData: AnalysisData = AnalysisData(),
    val availableCurrency: List<String> = emptyList(),
    val selectedCheckbox: List<String> = emptyList(),
    val category: Category? = null,
    val balanceBar: BalanceBar = BalanceBar(),
    val graphType: GraphType = GraphType.SlimDonut,
    val isChoosingFilter: Boolean = false,
    val filterState: FilterState = FilterState()
)

data class AnalysisData(
    val categoriesWithAmountResource: Resource<List<CategoryWithAmount>> = Resource.Loading(),
    val accountsWithAmountResource: Resource<List<AccountWithAmountType>> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>> = Resource.Loading(),
)

