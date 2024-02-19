package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Repository.CurrencyRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.Event.AddRecordEvent
import com.fredy.mysavings.ViewModels.Event.CalcEvent
import com.fredy.mysavings.ViewModels.Event.CalcOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlin.math.absoluteValue


@HiltViewModel
class AddSingleRecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {
    var state by mutableStateOf(AddRecordState())
    var calcState by mutableStateOf(CalcState())

    val resource = MutableStateFlow<Resource<String>>(
        Resource.Loading()
    )

    fun onEvent(event: AddRecordEvent) {
        when (event) {
            is AddRecordEvent.SetId -> {
                if (event.id != "-1" && state.isFirst) {
                    viewModelScope.launch {
                        recordRepository.getRecordById(
                            event.id
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
                                recordNotes = it.record.recordNotes,
                                isFirst = !state.isFirst
                            )
                            calcState = calcState.copy(
                                number1 = it.record.recordAmount.absoluteValue.toString()
                            )
                        }
                    }
                }
            }

            is AddRecordEvent.SaveRecord -> {
                resource.update {
                    Resource.Loading()
                }

                val record = performRecordCalculation()
                record?.let {
                    viewModelScope.launch {
                        recordRepository.upsertRecordItem(
                            record
                        )
                    }
                }?:return

                resource.update {
                    Resource.Success("Record Data Successfully Added")
                }

                state = AddRecordState()
                event.sideEffect()
            }

            is AddRecordEvent.AccountIdFromFk -> {
                state = state.copy(
                    recordCurrency = event.fromAccount.accountCurrency,
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

            is AddRecordEvent.RecordAmount -> {//useless
                state = state.copy(
                    recordAmount = calcState.number1.toDouble()//useless
                )
            }

            is AddRecordEvent.RecordCurrency -> {//useless
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

            is AddRecordEvent.DismissWarning -> {
                state = state.copy(
                    isShowWarning = false
                )
            }

            is AddRecordEvent.ConvertCurrency -> {
                viewModelScope.launch {
                    val balanceItem = currencyRepository.convertCurrencyData(
                        calcState.number1.toDouble().absoluteValue,
                        state.fromAccount.accountCurrency,
                        state.toAccount.accountCurrency
                    )
                    calcState = calcState.copy(
                        number1 = balanceItem.amount.toString()
                    )
                    state = state.copy(
                        recordCurrency = balanceItem.currency,
                        isAgreeToConvert = true
                    )
                }
            }
        }
    }

    private fun performRecordCalculation(): Record?{
        performCalculation()
        val recordId = state.recordId
        val accountIdFromFk = state.accountIdFromFk
        var accountIdToFk = state.accountIdToFk
        var categoryIdToFk = state.categoryIdFk
        val recordDateTime = state.recordDate.atTime(
            state.recordTime
        )
        var calculationResult = calcState.number1.toDouble().absoluteValue
        val recordCurrency = state.recordCurrency
        val fromAccountCurrency = state.fromAccount.accountCurrency
        val toAccountCurrency = state.toAccount.accountCurrency
        val recordType = state.recordType
        val recordNotes = state.recordNotes
        val previousAmount = state.previousAmount.absoluteValue
        var difference = state.recordAmount.absoluteValue
        if (recordId == "") {
            difference = calculationResult
        } else {
            difference -= calculationResult
            difference = -difference
        }

        if (isTransfer(recordType)) {
            categoryIdToFk = "1"
        } else {
            accountIdToFk = accountIdFromFk
        }

        if (recordDateTime == null || calculationResult == 0.0 || recordCurrency.isBlank() || accountIdFromFk == null || accountIdToFk == null || categoryIdToFk == null) {
            resource.update {
                Resource.Error(
                    "Please fill all required information"
                )
            }
            return null
        }
        if ((state.fromAccount.accountAmount < difference && isExpense(recordType)) || (state.fromAccount.accountAmount < difference && isTransfer(recordType) && fromAccountCurrency == toAccountCurrency)) {
            resource.update {
                Resource.Error(
                    "Account balance is not enough"
                )
            }
            return null
        }
        if ((recordType != state.toCategory.categoryType && !isTransfer(
                recordType
            ))
        ) {
            resource.update {
                Resource.Error(
                    "Record Type is not the same with category type"
                )
            }
            return null
        }
        if (isTransfer(recordType)) {
            if (state.fromAccount.accountAmount < difference && fromAccountCurrency != toAccountCurrency) {
                resource.update {
                    Resource.Error(
                        "Account balance is not enough"
                    )
                }
                return null
            }
            if (state.fromAccount == state.toAccount) {
                resource.update {
                    Resource.Error(
                        "You Can't transfer into the same account"
                    )
                }
                return null
            }
            if (!state.isAgreeToConvert && fromAccountCurrency != toAccountCurrency) {
                resource.update {
                    Resource.Error(
                        "Account Currencies Are not The same!!!, " + "Are you sure want to Transfer from $fromAccountCurrency Currency to ${toAccountCurrency} Currency? \n(Result Will be Converted)"
                    )
                }
                state = state.copy(
                    isShowWarning = true,
                    previousAmount = calculationResult
                )
                return null
            }
        }

        if (isExpense(recordType)) {
            state.fromAccount.accountAmount -= difference
            calculationResult = -calculationResult
        } else if (isTransfer(recordType) && fromAccountCurrency == toAccountCurrency) {
            state.fromAccount.accountAmount -= difference
            state.toAccount.accountAmount += difference
        } else if (isTransfer(recordType) && fromAccountCurrency != toAccountCurrency) {
            state.fromAccount.accountAmount -= previousAmount
            state.toAccount.accountAmount += difference
        } else {
            state.fromAccount.accountAmount += difference
        }

        return Record(
            recordId = recordId,
            accountIdFromFk = accountIdFromFk,
            accountIdToFk = accountIdToFk,
            categoryIdFk = categoryIdToFk,
            recordDateTime = recordDateTime,
            recordAmount = calculationResult,
            recordCurrency = recordCurrency,
            recordType = recordType,
            recordNotes = recordNotes,
        )
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
                number2 = calcState.number2.dropLast(
                    1
                )
            )

            calcState.operation != null -> calcState = calcState.copy(
                operation = null
            )

            calcState.number1.length == 1 && calcState.number1 != "0" -> calcState = calcState.copy(
                number1 = "0"
            )

            calcState.number1.isNotBlank() && calcState.number1 != "0" -> calcState =
                calcState.copy(
                    number1 = calcState.number1.dropLast(
                        1
                    )
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
            ) && calcState.number1.isNotBlank()
        ) {
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
            ) && calcState.number1.isNotBlank()
        ) {
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
            ) && calcState.number1.isNotBlank()
        ) {
            calcState = calcState.copy(
                number1 = calcState.number1.dropLast(
                    2
                )
            )
            return
        }
        if (calcState.number2.endsWith(".0") && calcState.number2.isNotBlank()) {
            calcState = calcState.copy(
                number2 = calcState.number2.dropLast(
                    2
                )
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


data class AddRecordState(
    val recordId: String = "",
    val accountIdFromFk: String? = null,
    val fromAccount: Account = Account(),
    val accountIdToFk: String? = null,
    val toAccount: Account = Account(),
    val categoryIdFk: String? = null,
    val toCategory: Category = Category(),
    val recordDate: LocalDate = LocalDate.now(),
    val recordTime: LocalTime = LocalTime.now(),
    val recordAmount: Double = 0.0,
    val previousAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordNotes: String = "",
    val recordType: RecordType = RecordType.Expense,
    val isFirst: Boolean = true,
    val isShowWarning: Boolean = false,
    val isAgreeToConvert: Boolean = false,
)

data class CalcState(
    val number1: String = "0",
    val number2: String = "",
    val operation: CalcOperation? = null
)