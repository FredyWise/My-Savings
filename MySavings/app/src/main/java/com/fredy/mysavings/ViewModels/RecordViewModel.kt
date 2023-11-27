package com.fredy.mysavings.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Enum.FilterType
import com.fredy.mysavings.Data.Database.Enum.RecordType
import com.fredy.mysavings.Data.Database.Enum.SortType
import com.fredy.mysavings.Repository.RecordRepository
import com.fredy.mysavings.Repository.TrueRecord
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject


@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
): ViewModel() {
    private val _sortType = MutableStateFlow(
        SortType.DESCENDING
    )

    private val _filterState = MutableStateFlow(
        FilterState()
    )

    private val _records = _filterState.flatMapLatest { filterType ->
        when (filterType.filterType) {
            FilterType.Daily -> recordRepository.getUserRecordsFromSpecificTime(
                filterType.start, filterType.end
            )

            FilterType.Weekly -> recordRepository.getUserRecordsFromSpecificTime(
                filterType.start, filterType.end
            )

            FilterType.Monthly -> recordRepository.getUserRecordsFromSpecificTime(
                filterType.start, filterType.end
            )

            FilterType.Per3Months -> recordRepository.getUserRecordsFromSpecificTime(
                filterType.start, filterType.end
            )

            FilterType.Per6Months -> recordRepository.getUserRecordsFromSpecificTime(
                filterType.start, filterType.end
            )

            FilterType.Yearly -> recordRepository.getUserRecordsFromSpecificTime(
                filterType.start, filterType.end
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
        _filterState,
        _records,
        balanceBar,
    ) { state, sortType, filterType, records, balanceBar ->
        state.copy(
            trueRecords = records.groupBy {
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
                updateFilterState(event.filterType)
            }

            is RecordsEvent.ShowNextList -> {
                when (state.value.filterType) {
                    FilterType.Daily -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.plusDays(
                                1
                            ),
                        )
                    }

                    FilterType.Weekly -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.plusWeeks(
                                1
                            ),
                        )
                    }

                    FilterType.Monthly -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.plusMonths(
                                1
                            ),
                        )
                    }

                    FilterType.Per3Months -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.plusMonths(
                                3
                            ),
                        )
                    }

                    FilterType.Per6Months -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.plusMonths(
                                6
                            ),
                        )
                    }

                    FilterType.Yearly -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.plusYears(
                                1
                            ),
                        )
                    }
                }
                updateFilterState(state.value.filterType)
            }

            is RecordsEvent.ShowPreviousList -> {
                when (state.value.filterType) {
                    FilterType.Daily -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.minusDays(
                                1
                            ),
                        )
                    }

                    FilterType.Weekly -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.minusWeeks(
                                1
                            ),
                        )
                    }

                    FilterType.Monthly -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.minusMonths(
                                1
                            ),
                        )
                    }

                    FilterType.Per3Months -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.minusMonths(
                                3
                            ),
                        )
                    }

                    FilterType.Per6Months -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.minusMonths(
                                6
                            ),
                        )
                    }

                    FilterType.Yearly -> _state.update {
                        it.copy(
                            chosenDate = it.chosenDate.minusYears(
                                1
                            ),
                        )
                    }
                }
                updateFilterState(state.value.filterType)
            }


        }
    }

    private fun updateFilterState(event: FilterType) {
        when (event) {
            FilterType.Daily -> _filterState.value = FilterState(
                FilterType.Daily,
                LocalDateTime.of(
                    state.value.chosenDate,
                    LocalTime.MIN
                ),
                LocalDateTime.of(
                    state.value.chosenDate,
                    LocalTime.MAX
                )
            )


            FilterType.Weekly -> _filterState.value = FilterState(
                FilterType.Weekly,
                LocalDateTime.of(
                    state.value.chosenDate.with(
                        TemporalAdjusters.previousOrSame(
                            DayOfWeek.MONDAY
                        )
                    ), LocalTime.MIN
                ),
                LocalDateTime.of(
                    state.value.chosenDate.with(
                        TemporalAdjusters.previousOrSame(
                            DayOfWeek.SUNDAY
                        )
                    ), LocalTime.MAX
                )
            )

            FilterType.Monthly -> _filterState.value = FilterState(
                FilterType.Monthly,
                LocalDateTime.of(
                    state.value.chosenDate.with(
                        TemporalAdjusters.firstDayOfMonth()
                    ), LocalTime.MIN
                ),
                LocalDateTime.of(
                    state.value.chosenDate.with(
                        TemporalAdjusters.lastDayOfMonth()
                    ), LocalTime.MAX
                )
            )

            FilterType.Per3Months -> _filterState.value = FilterState(
                FilterType.Monthly,
                LocalDateTime.of(
                    state.value.chosenDate.with(
                        TemporalAdjusters.firstDayOfMonth()
                    ), LocalTime.MIN
                ),
                LocalDateTime.of(
                    state.value.chosenDate.plusMonths(
                        2
                    ).with(
                        TemporalAdjusters.lastDayOfMonth()
                    ), LocalTime.MAX
                )
            )

            FilterType.Per6Months -> _filterState.value = FilterState(
                FilterType.Monthly,
                LocalDateTime.of(
                    state.value.chosenDate.with(
                        TemporalAdjusters.firstDayOfMonth()
                    ), LocalTime.MIN
                ),
                LocalDateTime.of(
                    state.value.chosenDate.plusMonths(
                        5
                    ).with(
                        TemporalAdjusters.lastDayOfMonth()
                    ), LocalTime.MAX
                )
            )

            FilterType.Yearly -> _filterState.value = FilterState(
                FilterType.Yearly,
                LocalDateTime.of(
                    state.value.chosenDate.with(
                        TemporalAdjusters.firstDayOfYear()
                    ), LocalTime.MIN
                ),
                LocalDateTime.of(
                    state.value.chosenDate.with(
                        TemporalAdjusters.lastDayOfYear()
                    ), LocalTime.MAX
                )
            )
        }
    }
}

data class RecordState(
    val trueRecords: List<RecordMap> = listOf(),
    val trueRecord: TrueRecord? = null,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalAll: Double = 0.0,
    val sortType: SortType = SortType.ASCENDING,
    val chosenDate: LocalDate = LocalDate.now(),
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