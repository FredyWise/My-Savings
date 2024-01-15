package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Data.Enum.GraphType
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.CategoryWithAmount
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.minusFilterDate
import com.fredy.mysavings.Util.plusFilterDate
import com.fredy.mysavings.Util.updateFilterState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
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
        when (filterState.filterType) {
            FilterType.Daily -> recordRepository.getUserCategoriesWithAmountFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies,
            )

            FilterType.Weekly -> recordRepository.getUserCategoriesWithAmountFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies
            )

            FilterType.Monthly -> recordRepository.getUserCategoriesWithAmountFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies
            )

            FilterType.Per3Months -> recordRepository.getUserCategoriesWithAmountFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies
            )

            FilterType.Per6Months -> recordRepository.getUserCategoriesWithAmountFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies
            )

            FilterType.Yearly -> recordRepository.getUserCategoriesWithAmountFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _recordsWithinSpecificTime = _filterState.flatMapLatest { filterState ->
        when (filterState.filterType) {
            FilterType.Daily -> recordRepository.getUserRecordsFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies,
            )

            FilterType.Weekly -> recordRepository.getUserRecordsFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies,
            )

            FilterType.Monthly -> recordRepository.getUserRecordsFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies,
            )

            FilterType.Per3Months -> recordRepository.getUserRecordsFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies,
            )

            FilterType.Per6Months -> recordRepository.getUserRecordsFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies,
            )

            FilterType.Yearly -> recordRepository.getUserRecordsFromSpecificTime(
                filterState.recordType,
                filterState.start,
                filterState.end,
                filterState.currencies,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )


    private val _totalRecordBalance: StateFlow<Double?> = recordRepository.getUserTotalRecordBalance().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0.0
    )

    private val _totalExpense: StateFlow<Double?> = recordRepository.getUserTotalAmountByType(
        RecordType.Expense
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0.0
    )

    private val _totalIncome: StateFlow<Double?> = recordRepository.getUserTotalAmountByType(
        RecordType.Income
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0.0
    )

    private val _balanceBar = MutableStateFlow(
        BalanceBar()
    )

    private val balanceBar = combine(
        _balanceBar,
        _totalExpense,
        _totalIncome,
        _totalRecordBalance
    ) { balanceBar, totalExpense, totalIncome, totalRecordBalance ->
        balanceBar.copy(
            expense = totalExpense ?: 0.0,
            income = totalIncome ?: 0.0,
            balance = totalRecordBalance ?: 0.0,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BalanceBar()
    )

    private val _state = MutableStateFlow(
        AnalysisState()
    )

    val state = combine(
        _state,
        balanceBar,
        _categoriesWithAmount,
        _recordsWithinSpecificTime,
        _availableCurrency,
    ) { state, balanceBar, categoriesWithAmount, recordsWithinSpecificTime, availableCurrency ->
        state.copy(
            categoriesWithAmountResource = categoriesWithAmount,
            recordsWithinTimeResource = recordsWithinSpecificTime,
            availableCurrency = availableCurrency,
            totalExpense = balanceBar.expense,
            totalIncome = balanceBar.income,
            totalAll = balanceBar.balance,
            graphAmount = if (isExpense(state.recordType)) balanceBar.expense else balanceBar.income,
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
                _state.update {
                    it.copy(
                        filterType = event.filterType
                    )
                }
            }

            is AnalysisEvent.ShowNextList -> {
                _state.update {
                    it.copy(
                        selectedDate = plusFilterDate(
                            state.value.filterType,
                            it.selectedDate
                        )
                    )
                }
            }

            is AnalysisEvent.ShowPreviousList -> {
                _state.update {
                    it.copy(
                        selectedDate = minusFilterDate(
                            state.value.filterType,
                            it.selectedDate
                        )
                    )
                }
            }

            is AnalysisEvent.ChangeDate -> {
                _state.update {
                    it.copy(
                        selectedDate = event.selectedDate
                    )
                }
            }

            is AnalysisEvent.ToggleRecordType -> {
                _state.update {
                    it.copy(
                        recordType = if (isExpense(
                                it.recordType
                            )) {
                            RecordType.Income
                        } else {
                            RecordType.Expense
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
        }
        _filterState.update {
            updateFilterState(
                state.value.filterType,
                state.value.selectedDate,
                it.copy(recordType = _state.value.recordType)
            )
        }
    }

}

data class AnalysisState(
    val categoriesWithAmountResource: Resource<List<CategoryWithAmount>> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>> = Resource.Loading(),
    val availableCurrency: List<String> = emptyList(),
    val selectedCheckbox: List<String> = emptyList(),
    val category: Category? = null,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalAll: Double = 0.0,
    val graphAmount: Double = totalExpense,
    val graphType: GraphType = GraphType.SlimDonut,
    val recordType: RecordType = RecordType.Expense,
    val selectedDate: LocalDate = LocalDate.now(),
    val isChoosingFilter: Boolean = false,
    val filterType: FilterType = FilterType.Monthly
)


