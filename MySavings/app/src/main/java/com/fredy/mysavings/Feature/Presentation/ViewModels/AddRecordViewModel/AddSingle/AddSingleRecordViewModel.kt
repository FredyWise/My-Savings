package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddSingle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.transferCategory
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordState
import com.fredy.mysavings.Util.Log
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

    val resource = MutableStateFlow<Resource<AddRecordState>>(
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
                                walletIdFromFk = it.record.walletIdFromFk,
                                walletIdToFk = it.record.walletIdToFk,
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
                    viewModelScope.launch {
                        performCalculation()
                        recordUseCases.upsertRecordItem(state.copy(recordAmount = calcState.number1.toDouble())).collect { result ->
                            Log.i(result.message + "\n" + result.data)
                            resource.update { result }
                            if (result is Resource.Success) {
                                state = result.data!!
                                if (state.previousAmount != 0.0 && !state.isAgreeToConvert){
                                    return@collect
                                }
                                event.sideEffect()
                                state = AddRecordState()
                            }
                        }
                    }
                }

                is AddRecordEvent.AccountIdFromFk -> {
                    state = state.copy(
                        recordCurrency = event.fromWallet.walletCurrency,
                        walletIdFromFk = event.fromWallet.walletId,
                        fromWallet = event.fromWallet
                    )
                }

                is AddRecordEvent.AccountIdToFk -> {
                    state = state.copy(
                        walletIdToFk = event.toWallet.walletId,
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

                is AddRecordEvent.RecordAmount -> {
                    state = state.copy(
                        recordAmount = calcState.number1.toDouble()
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

                else -> {}
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


