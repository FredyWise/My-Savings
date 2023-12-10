package com.fredy.mysavings.ViewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Repository.RecordRepository
import com.fredy.mysavings.Repository.TrueRecord
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.minusFilterDate
import com.fredy.mysavings.Util.plusFilterDate
import com.fredy.mysavings.Util.updateFilterState
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
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
): ViewModel() {
    private val _resource = mutableStateOf(
        ResourceState()
    )
    val resource: State<ResourceState> = _resource

    private val _sortType = MutableStateFlow(
        SortType.DESCENDING
    )

    private val _filterState = MutableStateFlow(
        FilterState()
    )

    private val _records = _filterState.flatMapLatest { filterState ->
        when (filterState.filterType) {
            FilterType.Daily -> recordRepository.getUserTrueRecordsFromSpecificTime(
                TimestampConverter.fromDateTime(
                    filterState.start
                ),
                TimestampConverter.fromDateTime(
                    filterState.end
                )
            )

            FilterType.Weekly -> recordRepository.getUserTrueRecordsFromSpecificTime(
                TimestampConverter.fromDateTime(
                    filterState.start
                ),
                TimestampConverter.fromDateTime(
                    filterState.end
                )
            )

            FilterType.Monthly -> recordRepository.getUserTrueRecordsFromSpecificTime(
                TimestampConverter.fromDateTime(
                    filterState.start
                ),
                TimestampConverter.fromDateTime(
                    filterState.end
                )
            )

            FilterType.Per3Months -> recordRepository.getUserTrueRecordsFromSpecificTime(
                TimestampConverter.fromDateTime(
                    filterState.start
                ),
                TimestampConverter.fromDateTime(
                    filterState.end
                )
            )

            FilterType.Per6Months -> recordRepository.getUserTrueRecordsFromSpecificTime(
                TimestampConverter.fromDateTime(
                    filterState.start
                ),
                TimestampConverter.fromDateTime(
                    filterState.end
                )
            )

            FilterType.Yearly -> recordRepository.getUserTrueRecordsFromSpecificTime(
                TimestampConverter.fromDateTime(
                    filterState.start
                ),
                TimestampConverter.fromDateTime(
                    filterState.end
                )
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
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
        RecordState()
    )

    val state = combine(
        _state,
        _sortType,
        _records,
        balanceBar,
    ) { state, sortType, records, balanceBar ->
        _resource.value = ResourceState(isLoading = true)
        state.copy(
            trueRecordMaps = records.groupBy {
                it.record.recordDateTime.toLocalDate()
            }.toSortedMap(if (sortType == SortType.DESCENDING) {
                compareByDescending { it }
            } else {
                compareBy { it }
            }).map {
                RecordMap(
                    recordDate = it.key,
                    records = it.value
                )
            },
            totalExpense = balanceBar.expense,
            totalIncome = balanceBar.income,
            totalAll = balanceBar.balance,
            sortType = sortType,
        )
    }.onCompletion {
        _resource.value = ResourceState(isLoading = false)
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
        }
        _filterState.value = updateFilterState(
            state.value.filterType,
            state.value.selectedDate
        )
    }

}

data class RecordState(
    val trueRecordMaps: List<RecordMap> = listOf(),
    val trueRecord: TrueRecord? = null,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalAll: Double = 0.0,
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
    val expense: Double = 0.0,
    val income: Double = 0.0,
    val balance: Double = 0.0,
)

data class FilterState(
    val recordType: RecordType = RecordType.Expense,
    val filterType: FilterType = FilterType.Monthly,
    val start: LocalDateTime = LocalDateTime.of(
        LocalDate.now().with(
            TemporalAdjusters.firstDayOfMonth()
        ), LocalTime.MIN
    ),
    val end: LocalDateTime = LocalDateTime.of(
        LocalDate.now().with(
            TemporalAdjusters.lastDayOfMonth()
        ), LocalTime.MIN
    ),
)