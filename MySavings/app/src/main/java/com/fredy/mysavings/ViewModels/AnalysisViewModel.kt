package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Data.Enum.GraphType
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.AccountWithAmountType
import com.fredy.mysavings.Data.Repository.CategoryWithAmount
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.map
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
        filterState.map { start, end, recordType, _, currencies ->
            recordRepository.getUserCategoriesWithAmountFromSpecificTime(
                recordType,
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
        filterState.map { start, end, _, _, _ ->
            recordRepository.getUserAccountsWithAmountFromSpecificTime(
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
        filterState.map { start, end, recordType, _, currencies ->
            recordRepository.getUserRecordsFromSpecificTime(
                recordType,
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


    private val _totalRecordBalance= recordRepository.getUserTotalRecordBalance().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalExpense= recordRepository.getUserTotalAmountByType(
        RecordType.Expense
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalIncome= recordRepository.getUserTotalAmountByType(
        RecordType.Income
    ).stateIn(
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
        _totalRecordBalance
    ) { balanceBar, totalExpense, totalIncome, totalRecordBalance ->
        balanceBar.copy(
            expense = totalExpense,
            income = totalIncome,
            balance = totalRecordBalance,
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
    ) { state, balanceBar, analysisData, availableCurrency ->
        state.copy(
            categoriesWithAmountResource = analysisData.categoriesWithAmountResource,
            recordsWithinTimeResource = analysisData.recordsWithinTimeResource,
            accountsWithAmountResource = analysisData.accountsWithAmountResource,
            availableCurrency = availableCurrency,
            balanceBar = balanceBar,
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
    val accountsWithAmountResource: Resource<List<AccountWithAmountType>> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>> = Resource.Loading(),
    val availableCurrency: List<String> = emptyList(),
    val selectedCheckbox: List<String> = emptyList(),
    val category: Category? = null,
    val balanceBar: BalanceBar = BalanceBar(),
    val graphType: GraphType = GraphType.SlimDonut,
    val recordType: RecordType = RecordType.Expense,
    val selectedDate: LocalDate = LocalDate.now(),
    val isChoosingFilter: Boolean = false,
    val filterType: FilterType = FilterType.Monthly
)

data class AnalysisData(
    val categoriesWithAmountResource: Resource<List<CategoryWithAmount>> = Resource.Loading(),
    val accountsWithAmountResource: Resource<List<AccountWithAmountType>> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>> = Resource.Loading(),
)

