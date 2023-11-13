package com.fredy.mysavings.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import com.fredy.mysavings.Data.RoomDatabase.Event.RecordsEvent
import com.fredy.mysavings.ui.Repository.Graph
import com.fredy.mysavings.ui.Repository.RecordRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    val state = combine(
        _state, _sortType, _records
    ) { state, sortType, records ->
        state.copy(trueRecords = records.groupBy {
            it.record.recordDateTime
        }.toSortedMap().map {
            RecordMap(
                recordDateTime = it.key,
                records = it.value
            )
        }, sortType = sortType
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

            is RecordsEvent.SaveRecord -> {
                val recordId = state.value.recordId
                val accountIdFromFk = state.value.accountIdFromFk
                val categoryIdToFk = state.value.categoryIdFk
                val recordDateTime = state.value.recordDateTime
                val recordAmount = state.value.recordAmount
                val recordCurrency = state.value.recordCurrency
                val recordNotes = state.value.recordNotes

                if (recordDateTime == null || recordAmount == 0.0 || recordCurrency.isBlank() || accountIdFromFk == null || categoryIdToFk == null) {
                    return
                }

                val record = Record(
                    recordId = recordId!!,
                    accountIdFromFk = accountIdFromFk,
                    categoryIdFk = categoryIdToFk,
                    recordDateTime = recordDateTime,
                    recordAmount = recordAmount,
                    recordCurrency = recordCurrency,
                    recordNotes = recordNotes,
                )
                viewModelScope.launch {
                    recordRepository.upsertRecordItem(
                        record
                    )
                }
                _state.update {
                    it.copy(
                        recordId = null,
                        accountIdFromFk = null,
//                        accountIdToFk = null,
                        categoryIdFk = null,
                        recordDateTime = null,
                        recordAmount = 0.0,
                        recordCurrency = "",
                        recordNotes = ""
                    )
                }
            }

            is RecordsEvent.AccountIdFromFk -> {
                _state.update {
                    it.copy(
                        accountIdFromFk = event.fromAccount
                    )
                }
            }

            is RecordsEvent.CategoryIdFk -> {
                _state.update {
                    it.copy(
                        categoryIdFk = event.toCategory
                    )
                }
            }

            is RecordsEvent.RecordDateTime -> {
                _state.update {
                    it.copy(
                        recordDateTime = event.dateTime
                    )
                }
            }

            is RecordsEvent.RecordAmount -> {
                _state.update {
                    it.copy(
                        recordAmount = event.amount
                    )
                }
            }

            is RecordsEvent.RecordCurrency -> {
                _state.update {
                    it.copy(
                        recordCurrency = event.currency
                    )
                }
            }

            is RecordsEvent.RecordNotes -> {
                _state.update {
                    it.copy(
                        recordNotes = event.notes
                    )
                }
            }

            is RecordsEvent.SortRecord -> {
                _sortType.value = event.sortType
            }

            is RecordsEvent.Dummy -> {
                val record = Record(
                    accountIdFromFk = 1,
                    categoryIdFk = 1,
                    recordDateTime = LocalDateTime.now(),
                    recordAmount = 10.0,
                    recordCurrency = "Piglins",
                    recordNotes = "recordNotes"
                )
                viewModelScope.launch {
                    Log.e(
                        "BABI",
                        "onEvent: " + state.value + _records.value
                    )
                    recordRepository.upsertRecordItem(
                        record
                    )
                }
            }
        }

    }
}

data class RecordState(
    val trueRecords: List<RecordMap> = listOf(),
    val trueRecord: TrueRecord? = null,
    val recordId: Int? = null,
    val accountIdFromFk: Int? = null,
//    val accountIdToFk: Int? = null,
    val categoryIdFk: Int? = null,
    val recordDateTime: LocalDateTime? = null,
    val recordAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordNotes: String = "",
//    val isAddingRecord: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)

data class RecordMap(
    val recordDateTime: LocalDateTime,
    val records: List<TrueRecord>
)