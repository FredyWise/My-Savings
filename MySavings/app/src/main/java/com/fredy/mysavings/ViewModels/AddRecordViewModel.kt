package com.fredy.mysavings.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.Event.AddRecordEvent
import com.fredy.mysavings.Data.RoomDatabase.Event.CalcEvent
import com.fredy.mysavings.Data.RoomDatabase.Event.CalcOperation
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.DI.AppModuleImpl
import com.fredy.mysavings.Repository.RecordRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.absoluteValue

class AddRecordViewModel(
    private val itemId: Int,
    private val recordRepository: RecordRepository = AppModuleImpl.recordRepository,
): ViewModel() {
    var state by mutableStateOf(AddRecordState())
    var calcState by mutableStateOf(CalcState())

    init {
        if (itemId != -1) {
            viewModelScope.launch {
                recordRepository.getRecordById(
                    itemId
                ).collectLatest {
                    state = state.copy(
                        fromAccount = it.fromAccount,
                        toAccount = it.toAccount,
                        toCategory = it.toCategory,
                        recordId = it.record.recordId,
                        accountIdFromFk = it.record.accountIdFromFk,
                        accountIdToFk = it.record.accountIdToFk,
                        categoryIdFk = it.record.categoryIdFk,
                        recordDate = it.record.recordDateTime.toLocalDate(),
                        recordTime = it.record.recordDateTime.toLocalTime(),
                        recordAmount = it.record.recordAmount,
                        recordCurrency = it.record.recordCurrency,
                        recordType = it.record.recordType,
                        recordNotes = it.record.recordNotes
                    )
                    calcState = calcState.copy(number1 = it.record.recordAmount.toString())
                }
            }
        }
    }

    fun onEvent(event: AddRecordEvent) {
        when (event) {
            is AddRecordEvent.SaveRecord -> {
                val recordId = state.recordId
                val accountIdFromFk = state.accountIdFromFk
                var accountIdToFk = state.accountIdToFk
                var categoryIdToFk = state.categoryIdFk
                val recordDateTime = state.recordDate.atTime(
                    state.recordTime
                )
                var recordAmount = calcState.number1.toDouble().absoluteValue
                val recordCurrency = "baba"//state.recordCurrency
                val recordType = state.recordType
                val recordNotes = state.recordNotes
                var difference = state.recordAmount.absoluteValue
                if (recordId == 0){
                    difference += recordAmount
                }else{
                    difference -= recordAmount
                    difference = -difference
                }

                if (isTransfer(recordType)) {
                    categoryIdToFk = 1
                } else {
                    accountIdToFk = accountIdFromFk
                }
                Log.e("BABI", "onEvent: "+state+"\n"+difference+"\n"+recordAmount+categoryIdToFk )

                if (recordDateTime == null || recordAmount == 0.0 || recordCurrency.isBlank() || accountIdFromFk == null || accountIdToFk == null || categoryIdToFk == null || (recordType != state.toCategory.categoryType && !isTransfer(recordType)) || (state.fromAccount.accountAmount < difference && !isIncome(recordType))) {
                    return
                }

                if (isExpense(recordType)) {
                    state.fromAccount.accountAmount -= difference
                    recordAmount = -recordAmount
                }else if(isTransfer(recordType)){
                    state.fromAccount.accountAmount -= difference
                    state.toAccount.accountAmount += difference
                }else {
                    state.fromAccount.accountAmount += difference
                }

                val record = Record(
                    recordId = recordId,
                    accountIdFromFk = accountIdFromFk,
                    accountIdToFk = accountIdToFk,
                    categoryIdFk = categoryIdToFk,
                    recordDateTime = recordDateTime,
                    recordAmount = recordAmount,
                    recordCurrency = recordCurrency,
                    recordType = recordType,
                    isTransfer = recordType == RecordType.Transfer,
                    recordNotes = recordNotes,
                )

                viewModelScope.launch {
                    recordRepository.upsertRecordItem(
                        record
                    )
                }
                state = state.copy(
                    recordId = 0,
                    fromAccount = Account(),
                    accountIdFromFk = null,
                    accountIdToFk = null,
                    toCategory = Category(),
                    categoryIdFk = null,
                    recordDate = LocalDate.now(),
                    recordTime = LocalTime.now(),
                    recordAmount = 0.0,
                    recordCurrency = "",
                    recordType = RecordType.Expense,
                    recordNotes = ""
                )

                event.navigateUp()
            }

            is AddRecordEvent.AccountIdFromFk -> {
                state = state.copy(
                    accountIdFromFk = event.fromAccount.accountId,
                    fromAccount = event.fromAccount
                )

            }

            is AddRecordEvent.AccountIdToFk -> {
                state = state.copy(
                    accountIdToFk = event.toAccount.accountId,
                    toAccount = event.toAccount
                )

            }

            is AddRecordEvent.CategoryIdFk -> {
                state = state.copy(
                    categoryIdFk = event.toCategory.categoryId,
                    toCategory = event.toCategory
                )

            }

            is AddRecordEvent.RecordDate -> {
                state = state.copy(
                    recordDate = event.date
                )

            }

            is AddRecordEvent.RecordTime -> {
                state = state.copy(
                    recordTime = event.time
                )

            }

            is AddRecordEvent.RecordAmount -> {
                state = state.copy(
                    recordAmount = calcState.number1.toDouble()
                )

            }

            is AddRecordEvent.RecordCurrency -> {
                state = state.copy(
                    recordCurrency = event.currency
                )

            }

            is AddRecordEvent.RecordTypes -> {
                state = state.copy(
                    recordType = event.recordType,
                    toCategory = Category()
                )
            }


            is AddRecordEvent.RecordNotes -> {
                state = state.copy(
                    recordNotes = event.notes
                )

            }
        }
    }

    fun onAction(event: CalcEvent) {
        when (event) {
            is CalcEvent.Number -> enterNumber(
                event.number
            )
            is CalcEvent.DecimalPoint -> enterDecimal()
            is CalcEvent.Clear -> calcState = CalcState()
            is CalcEvent.Operation -> enterOperation(
                event.operation
            )
            is CalcEvent.Calculate -> performCalculation()
            is CalcEvent.Delete -> performDeletion()
            is CalcEvent.Percent -> performPercent()
        }

    }

    private fun performDeletion() {
        when {
            calcState.number2.isNotBlank() -> calcState = calcState.copy(
                number2 = calcState.number2.dropLast(1)
            )

            calcState.operation != null -> calcState = calcState.copy(
                operation = null
            )

            calcState.number1.length == 1 && calcState.number1 != "0" -> calcState = calcState.copy(
                number1 = "0"
            )

            calcState.number1.isNotBlank() && calcState.number1 != "0" -> calcState = calcState.copy(
                number1 = calcState.number1.dropLast(1)
            )
        }
    }

    private fun performCalculation() {
        val number1 = calcState.number1.toDoubleOrNull()
        val number2 = calcState.number2.toDoubleOrNull()
        if (number1 != null && number2 != null) {
            val result = when (calcState.operation) {
                is CalcOperation.Add -> number1 + number2
                is CalcOperation.Substract -> number1 - number2
                is CalcOperation.Multiply -> number1 * number2
                is CalcOperation.Divide -> number1 / number2
                null -> return
            }
            calcState = calcState.copy(
                number1 = result.toString().take(
                    10
                ), number2 = "", operation = null
            )
            prettier()
        }
    }

    private fun enterOperation(operation: CalcOperation) {
        if (calcState.number1.isNotBlank() && calcState.number2.isBlank()) {
            calcState = calcState.copy(operation = operation)
        } else if (calcState.number1.isNotBlank() && calcState.number2.isNotBlank()) {
            performCalculation()
            calcState = calcState.copy(operation = operation)
        }
    }

    private fun enterDecimal() {
        if (calcState.operation == null && !calcState.number1.contains(
                "."
            ) && calcState.number1.isNotBlank()) {
            calcState = calcState.copy(
                number1 = calcState.number1 + "."
            )
            return
        }
        if (!calcState.number2.contains(".") && calcState.number2.isNotBlank()) {
            calcState = calcState.copy(
                number2 = calcState.number2 + "."
            )
        }
    }

    private fun performPercent() {
        if (calcState.operation == null && !calcState.number1.contains(
                "%"
            ) && calcState.number1.isNotBlank()) {
            calcState = calcState.copy(
                number1 = (calcState.number1.toDouble() / 100).toString()
            )
            prettier()
            return
        }
        if (!calcState.number2.contains("%") && calcState.number2.isNotBlank()) {
            calcState = calcState.copy(
                number2 = (calcState.number2.toDouble() / 100).toString()
            )
            prettier()
        }
    }

    private fun prettier() {
        if (calcState.operation == null && calcState.number1.endsWith(
                ".0"
            ) && calcState.number1.isNotBlank()) {
            calcState = calcState.copy(
                number1 = calcState.number1.dropLast(2)
            )
            return
        }
        if (calcState.number2.endsWith(".0") && calcState.number2.isNotBlank()) {
            calcState = calcState.copy(
                number2 = calcState.number2.dropLast(2)
            )
        }
    }

    private fun enterNumber(number: String) {
        if (calcState.operation == null) {
            if (calcState.number1.length >= MAX_NUMBER_LENGTH) {
                return
            }
            if (calcState.number1 == "0") {
                calcState = calcState.copy(
                    number1 = ""
                )
            }
            calcState = calcState.copy(
                number1 = calcState.number1 + number
            )
            return
        }
        if (calcState.number2.length >= MAX_NUMBER_LENGTH) {
            return
        }
        if (calcState.number2 == "0") {
            calcState = calcState.copy(
                number2 = ""
            )
        }
        calcState = calcState.copy(
            number2 = calcState.number2 + number
        )
    }

    companion object {
        private const val MAX_NUMBER_LENGTH = 13
    }
}

@Suppress("UNCHECKED_CAST")
class AddRecordViewModelFactory(private val id: Int): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return AddRecordViewModel(itemId = id) as T
    }
}


data class AddRecordState(
    val recordId: Int = 0,
    val accountIdFromFk: Int? = null,
    val fromAccount: Account = Account(),
    val accountIdToFk: Int? = null,
    val toAccount: Account = Account(),
    val categoryIdFk: Int? = null,
    val toCategory: Category = Category(),
    val recordDate: LocalDate = LocalDate.now(),
    val recordTime: LocalTime = LocalTime.now(),
    val recordAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordNotes: String = "",
    val recordType: RecordType = RecordType.Expense
)

data class CalcState(
    val number1: String = "0",
    val number2: String = "",
    val operation: CalcOperation? = null
)