package com.fredy.mysavings.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Data.Repository.UserRepository
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.FilterState
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.map
import com.fredy.mysavings.Util.minusFilterDate
import com.fredy.mysavings.Util.plusFilterDate
import com.fredy.mysavings.Util.updateFilterState
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject


@HiltViewModel
class RecordViewModel @Inject constructor(
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


    private val _records = _filterState.flatMapLatest { filterState ->
        filterState.map { start, end, _, sortType, currencies ->
            recordRepository.getUserTrueRecordMapsFromSpecificTime(
                start,
                end,
                sortType,
                currencies
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

    private val _totalExpense = recordRepository.getUserTotalAmountByType(
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
        _totalBalance
    ) { balanceBar, totalExpense, totalIncome, totalBalance ->
        balanceBar.copy(
            expense = totalExpense,
            income = totalIncome,
            balance = totalBalance,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BalanceBar()
    )

    private val _state = MutableStateFlow(
        RecordState()
    )

    val state = combine(
        _state,
        _records,
        _availableCurrency,
        balanceBar,
    ) { state, records, availableCurrency, balanceBar ->
        state.copy(
            recordMapsResource = records,
            availableCurrency = availableCurrency,
            balanceBar = balanceBar
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
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

            is RecordsEvent.DeleteRecord -> {
                viewModelScope.launch {
                    recordRepository.deleteRecordItem(
                        event.record
                    )
                }
            }

            is RecordsEvent.FilterRecord -> {
                _state.update {
                    it.copy(
                        filterType = event.filterType
                    )
                }
            }

            is RecordsEvent.ShowNextList -> {
                _state.update {
                    it.copy(
                        selectedDate = plusFilterDate(
                            state.value.filterType,
                            it.selectedDate
                        )
                    )
                }
            }

            is RecordsEvent.ShowPreviousList -> {
                _state.update {
                    it.copy(
                        selectedDate = minusFilterDate(
                            state.value.filterType,
                            it.selectedDate
                        )
                    )
                }
            }

            is RecordsEvent.ChangeDate -> {
                _state.update {
                    it.copy(
                        selectedDate = event.selectedDate
                    )
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
        }
        _filterState.update {
            updateFilterState(
                state.value.filterType,
                state.value.selectedDate,
                it
            )
        }
    }

}

data class RecordState(
    val recordMapsResource: Resource<List<RecordMap>> = Resource.Loading(),
    val trueRecord: TrueRecord? = null,
    val availableCurrency: List<String> = listOf(),
    val selectedCheckbox: List<String> = listOf(),
    val balanceBar: BalanceBar = BalanceBar(),
    val sortType: SortType = SortType.ASCENDING,
    val selectedDate: LocalDate = LocalDate.now(),
    val isChoosingFilter: Boolean = false,
    val filterType: FilterType = FilterType.Monthly
)

data class RecordMap(
    val recordDate: LocalDate,
    val records: List<TrueRecord>
)

data class BalanceBar(
    val expense: BalanceItem = BalanceItem(),
    val income: BalanceItem = BalanceItem(),
    val balance: BalanceItem = BalanceItem(),
)

