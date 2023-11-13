package com.fredy.mysavings.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.Event.AddRecordEvent
import com.fredy.mysavings.ui.Repository.Graph
import com.fredy.mysavings.ui.Repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class AddRecordViewModel(
    private val itemId: Int,
    private val recordRepository: RecordRepository = Graph.recordRepository,
): ViewModel() {
    private val _record = recordRepository.getRecordById(
        itemId
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Record()
    )

    private val _state = MutableStateFlow(
        AddRecordState()
    )

    val state = combine(
        _state, _record,
    ) { state, record ->
        state.copy(
//            accountIdFromFk = record.accountIdFromFk,
//            categoryIdFk = record.categoryIdFk,
//            recordDate = record.recordDateTime.toLocalDate(),
//            recordTime = record.recordDateTime.toLocalTime(),
//            recordAmount = record.recordAmount,
//            recordCurrency = record.recordCurrency,
//            recordNotes = record.recordNotes
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AddRecordState()
    )

    fun onEvent(event: AddRecordEvent) {
        when (event) {
            is AddRecordEvent.SaveRecord -> {

                Log.e(
                    "BABI",
                    "onnt: " + state.value + _record.value
                )
//                val recordId = state.value.recordId
//                val accountIdFromFk = state.value.accountIdFromFk
//                val categoryIdToFk = state.value.categoryIdFk
//                val recordDateTime = state.value.recordDate.atTime(state.value.recordTime)
//                val recordAmount = state.value.recordAmount
//                val recordCurrency = state.value.recordCurrency
//                val recordType = state.value.recordType
//                val recordNotes = state.value.recordNotes
//
//                if (recordDateTime == null || recordAmount == 0.0 || recordCurrency.isBlank() || accountIdFromFk == null || categoryIdToFk == null) {
//                    return
//                }
//
//                val record = Record(
//                    recordId = recordId!!,
//                    accountIdFromFk = accountIdFromFk,
//                    categoryIdFk = categoryIdToFk,
//                    recordDateTime = recordDateTime,
//                    recordAmount = recordAmount,
//                    recordCurrency = recordCurrency,
//                    recordType = recordType,
//                    recordNotes = recordNotes,
//                )
//                viewModelScope.launch {
//                    recordRepository.upsertRecordItem(
//                        record
//                    )
//                }
//                _state.update {
//                    it.copy(
//                        recordId = null,
//                        accountIdFromFk = null,
////                        accountIdToFk = null,
//                        categoryIdFk = null,
//                        recordDate = LocalDate.now(),
//                        recordTime = LocalTime.now(),
//                        recordAmount = 0.0,
//                        recordCurrency = "",
//                        recordNotes = ""
//                    )
//                }
            }

            is AddRecordEvent.AccountIdFromFk -> {
                _state.update {
                    it.copy(
                        accountIdFromFk = event.fromAccount
                    )
                }
            }

            is AddRecordEvent.CategoryIdFk -> {
                _state.update {
                    it.copy(
                        categoryIdFk = event.toCategory
                    )
                }
            }

            is AddRecordEvent.RecordDate -> {
                _state.update {
                    it.copy(
                        recordDate= event.date
                    )
                }
            }

            is AddRecordEvent.RecordTime -> {
                _state.update {
                    it.copy(
                        recordTime = event.time
                    )
                }
            }

            is AddRecordEvent.RecordAmount -> {
                _state.update {
                    it.copy(
                        recordAmount = event.amount
                    )
                }
            }

            is AddRecordEvent.RecordCurrency -> {
                _state.update {
                    it.copy(
                        recordCurrency = event.currency
                    )
                }
            }

            is AddRecordEvent.RecordTypes -> {
                _state.update {
                    it.copy(
                        recordType = event.recordType
                    )
                }
            }

            is AddRecordEvent.RecordNotes -> {
                _state.update {
                    it.copy(
                        recordNotes = event.notes
                    )
                }
            }
        }
    }
}

class AddRecordViewModelFactory(private val id: Int): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddRecordViewModel(itemId = id) as T
    }
}


data class AddRecordState(
    val recordId: Int? = null,
    val accountIdFromFk: Int? = null,
//    val accountIdToFk: Int? = null,
    val categoryIdFk: Int? = null,
    val recordDate: LocalDate = LocalDate.now(),
    val recordTime: LocalTime = LocalTime.now(),
    val recordAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordType: RecordType = RecordType.Expense,
    val recordNotes: String = "",
//    val isAddingRecord: Boolean = false,
)