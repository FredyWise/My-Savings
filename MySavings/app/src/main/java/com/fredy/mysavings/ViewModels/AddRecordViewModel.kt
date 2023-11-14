package com.fredy.mysavings.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
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
    private val _trueRecord = recordRepository.getRecordById(
        itemId
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        TrueRecord()
    )

    private val _state = MutableStateFlow(
        AddRecordState()
    )

    val state = combine(
        _state,
        _trueRecord,
    ) { state, trueRecord ->
        if (itemId != -1) {
            state.copy(
                fromAccount = trueRecord.fromAccount,
                toCategory = trueRecord.toCategory,
                recordId = trueRecord.record.recordId,
                accountIdFromFk = trueRecord.record.accountIdFromFk,
                categoryIdFk = trueRecord.record.categoryIdFk,
                recordDate = trueRecord.record.recordDateTime.toLocalDate(),
                recordTime = trueRecord.record.recordDateTime.toLocalTime(),
                recordAmount = trueRecord.record.recordAmount,
                recordCurrency = trueRecord.record.recordCurrency,
                recordNotes = trueRecord.record.recordNotes
            )
        } else {
            state.copy()
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AddRecordState()
    )

    fun onEvent(event: AddRecordEvent) {
        when (event) {

            is AddRecordEvent.SaveRecord -> {
                val recordId = state.value.recordId
                val accountIdFromFk = state.value.accountIdFromFk
                val categoryIdToFk = state.value.categoryIdFk
                val recordDateTime = state.value.recordDate.atTime(
                    state.value.recordTime
                )
                val recordAmount = state.value.recordAmount
                val recordCurrency = "baba"//state.value.recordCurrency
                val recordType = state.value.recordType
                val recordNotes = state.value.recordNotes
                Log.e("BABI", "onEvent: "+state.value.toString(), )

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
                    recordType = recordType,
                    isTransfer = recordType == RecordType.Transfer,
                    recordNotes = recordNotes,
                )
                Log.e("BABI", "\n\nonLast: $record", )
                viewModelScope.launch {
                    recordRepository.upsertRecordItem(
                        record
                    )
                }
                _state.update {
                    it.copy(
                        recordId = null,
                        fromAccount = Account(),
                        accountIdFromFk = null,
//                        accountIdToFk = null,
                        toCategory = Category(),
                        categoryIdFk = null,
                        recordDate = LocalDate.now(),
                        recordTime = LocalTime.now(),
                        recordAmount = 0.0,
                        recordCurrency = "",
                        recordType = RecordType.Expense,
                        recordNotes = ""
                    )
                }
            }

            is AddRecordEvent.AccountIdFromFk -> {
                _state.update {
                    it.copy(
                        accountIdFromFk = event.fromAccount.accountId,
                        fromAccount = event.fromAccount
                    )
                }
            }

            is AddRecordEvent.CategoryIdFk -> {
                _state.update {
                    it.copy(
                        categoryIdFk = event.toCategory.categoryId,
                        toCategory = event.toCategory
                    )
                }
            }

            is AddRecordEvent.RecordDate -> {
                _state.update {
                    it.copy(
                        recordDate = event.date
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

@Suppress("UNCHECKED_CAST")
class AddRecordViewModelFactory(private val id: Int): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return AddRecordViewModel(itemId = id) as T
    }
}


data class AddRecordState(
    val recordId: Int? = 0,
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val accountIdFromFk: Int? = null,
    val fromAccount: Account = Account(),
//    val accountIdToFk: Int? = null,
//    val toAccount: Account = Account(),
    val categoryIdFk: Int? = null,
    val toCategory: Category = Category(),
    val recordDate: LocalDate = LocalDate.now(),
    val recordTime: LocalTime = LocalTime.now(),
    val recordAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordType: RecordType = RecordType.Expense,
    val recordNotes: String = ""
)