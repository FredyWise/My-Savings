package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.DefaultData.transferCategory
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isTransfer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue


@HiltViewModel
class AddSingleRecordViewModel @Inject constructor(
    private val recordUseCases: RecordUseCases,
    private val currencyUseCase: CurrencyUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(AddRecordState())
    var calcState by mutableStateOf(CalcState())

    val resource = MutableStateFlow<Resource<String>>(
        Resource.Loading()
    )

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("bookId")?.let { bookId ->
                savedStateHandle.get<String>("recordId")?.let { recordId ->
                    if (recordId != "-1") {
                        recordUseCases.getRecordById(
                            recordId
                        ).collectLatest {
                            state = state.copy(
                                fromWallet = it.fromWallet,
                                toWallet = it.toWallet,
                                toCategory = it.toCategory,
                                recordId = it.record.recordId,
                                accountIdFromFk = it.record.walletIdFromFk,
                                accountIdToFk = it.record.walletIdToFk,
                                categoryIdFk = it.record.categoryIdFk,
                                recordDate = it.record.recordDateTime.toLocalDate(),
                                recordTime = it.record.recordDateTime.toLocalTime(),
                                recordAmount = it.record.recordAmount,
                                recordCurrency = it.record.recordCurrency,
                                recordType = it.record.recordType,
                                recordNotes = it.record.recordNotes,
                            )
                            calcState = calcState.copy(
                                number1 = it.record.recordAmount.absoluteValue.toString()
                            )
                        }
                    }
                }
                state = state.copy(bookIdFk = bookId)
            }
        }
    }

    fun onEvent(event: AddRecordEvent) {
        viewModelScope.launch {
            when (event) {
                is AddRecordEvent.SaveRecord -> {
                    resource.update {
                        Resource.Loading()
                    }

                    val record = performRecordCalculation()
                    record?.let {
                        recordUseCases.upsertRecordItem(
                            record
                        )

                    } ?: return@launch

                    resource.update {
                        Resource.Success("Record Data Successfully Added")
                    }

                    state = AddRecordState()
                    event.sideEffect()
                }

                is AddRecordEvent.AccountIdFromFk -> {
                    state = state.copy(
                        recordCurrency = event.fromWallet.walletCurrency,
                        accountIdFromFk = event.fromWallet.walletId,
                        fromWallet = event.fromWallet
                    )
                }

                is AddRecordEvent.AccountIdToFk -> {
                    state = state.copy(
                        accountIdToFk = event.toWallet.walletId,
                        toWallet = event.toWallet
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
                        val balanceItem = currencyUseCase.convertCurrencyData(
                            calcState.number1.toDouble().absoluteValue,
                            state.fromWallet.walletCurrency,
                            state.toWallet.walletCurrency
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
    }

    private fun performRecordCalculation(): Record? {
        performCalculation()
        val recordId = state.recordId
        val accountIdFromFk = state.accountIdFromFk
        var accountIdToFk = state.accountIdToFk
        var categoryIdToFk = state.categoryIdFk
        val bookIdFk = state.bookIdFk
        val recordDateTime = state.recordDate.atTime(state.recordTime.withNano((state.recordTime.nano.div(1000000)).times(1000000)))
        var calculationResult = calcState.number1.toDouble().absoluteValue
        val recordCurrency = state.recordCurrency
        val fromAccountCurrency = state.fromWallet.walletCurrency
        val toAccountCurrency = state.toWallet.walletCurrency
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
            categoryIdToFk = transferCategory.categoryId
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

        if ((state.fromWallet.walletAmount < difference && isExpense(recordType)) || (state.fromWallet.walletAmount < difference && isTransfer(
                recordType
            ) && fromAccountCurrency == toAccountCurrency)
        ) {
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
            if (state.fromWallet.walletAmount < difference && fromAccountCurrency != toAccountCurrency) {
                resource.update {
                    Resource.Error(
                        "Account balance is not enough"
                    )
                }
                return null
            }
            if (state.fromWallet == state.toWallet) {
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
            state.fromWallet.walletAmount -= difference
            calculationResult = -calculationResult
        } else if (isTransfer(recordType) && fromAccountCurrency == toAccountCurrency) {
            state.fromWallet.walletAmount -= difference
            state.toWallet.walletAmount += difference
        } else if (isTransfer(recordType) && fromAccountCurrency != toAccountCurrency) {
            state.fromWallet.walletAmount -= previousAmount
            state.toWallet.walletAmount += difference
        } else {
            state.fromWallet.walletAmount += difference
        }

        return Record(
            recordId = recordId,
            accountIdFromFk = accountIdFromFk,
            accountIdToFk = accountIdToFk,
            categoryIdFk = categoryIdToFk,
            bookId = bookIdFk,
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


