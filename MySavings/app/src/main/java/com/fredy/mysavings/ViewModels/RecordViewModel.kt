package com.fredy.mysavings.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import com.fredy.mysavings.Data.RoomDatabase.Event.RecordsEvent
import com.fredy.mysavings.Data.isExpense
import com.fredy.mysavings.ui.Repository.Graph
import com.fredy.mysavings.ui.Repository.RecordRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RecordViewModel(
    private val recordRepository: RecordRepositoryImpl = Graph.recordRepository,
): ViewModel() {
    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _records = recordRepository.getUserRecordsOrderedDescending().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(
        RecordState()
    )

    init {
        viewModelScope.launch {
            recordRepository.getUserTotalAmountByType(
                RecordType.Expense
            ).collectLatest { totalExpense ->
                _state.update {
                    it.copy(
                        totalExpense = totalExpense,
                        totalAll = totalExpense + it.totalAll
                    )
                }
            }
        }
        viewModelScope.launch {
            recordRepository.getUserTotalAmountByType(
                RecordType.Income
            ).collectLatest { totalIncome ->
                _state.update {
                    it.copy(
                        totalIncome = totalIncome + it.totalIncome,
                        totalAll = totalIncome + it.totalAll
                    )
                }
            }
        }
    }

    val state = combine(
        _state, _sortType, _records
    ) { state, sortType, records ->
        state.copy(trueRecords = records.groupBy {
            it.record.recordDateTime
        }.toSortedMap(compareByDescending { it }).map {
            RecordMap(
                recordDateTime = it.key,
                records = it.value
            )
        },
            sortType = sortType
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

            is RecordsEvent.DeleteRecord -> {
                viewModelScope.launch {
                    recordRepository.deleteRecordItem(
                        event.record
                    )
                }
            }

            is RecordsEvent.SortRecord -> {
                _sortType.value = event.sortType
            }
        }

    }
}

data class RecordState(
    val trueRecords: List<RecordMap> = listOf(),
    val trueRecord: TrueRecord? = null,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalAll: Double = 0.0,
    val sortType: SortType = SortType.ASCENDING
)

data class RecordMap(
    val recordDateTime: LocalDateTime,
    val records: List<TrueRecord>
)