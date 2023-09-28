package com.fredy.mysavings.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fredy.mysavings.Data.Add.CalcAction
import com.fredy.mysavings.Data.Add.CalcOperation
import com.fredy.mysavings.Data.Add.CalcState

//state.number1 will be used as the saved data in the note
class addViewModel : ViewModel() {
    var state by mutableStateOf(CalcState())

    fun onAction (action : CalcAction){
        when(action) {
            is CalcAction.Number -> enterNumber(action.number)
            is CalcAction.DecimalPoint -> enterDecimal()
            is CalcAction.Clear -> state = CalcState()
            is CalcAction.Operation -> enterOperation(action.operation)
            is CalcAction.Calculate -> performCalculation()
            is CalcAction.Delete -> performDeletion()
            is CalcAction.Percent -> performPercent()
        }

    }
    private fun performDeletion() {
        when {
            state.number2.isNotBlank() -> state = state.copy(
                number2 = state.number2.dropLast(1)
            )
            state.operation != null -> state = state.copy(
                operation = null
            )
            state.number1.isNotBlank() -> state = state.copy(
                number1 =  state.number1.dropLast(1)
            )
        }
    }

    private fun performCalculation() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        if (number1 != null && number2 != null) {
            val result = when (state.operation) {
                is CalcOperation.Add -> number1 + number2
                is CalcOperation.Substract -> number1 - number2
                is CalcOperation.Multiply -> number1 * number2
                is CalcOperation.Divide -> number1 / number2
                null -> return
            }
            state = state.copy(
                number1 = result.toString().take(10),
                number2 = "",
                operation = null
            )
            prettier()
        }
    }

    private fun enterOperation(operation: CalcOperation) {
        if (state.number1.isNotBlank() && state.number2.isBlank()){
            state = state.copy(operation = operation)
        }else if (state.number1.isNotBlank() && state.number2.isNotBlank()){
            performCalculation()
            state = state.copy(operation = operation)
        }
    }

    private fun enterDecimal() {
        if (state.operation == null && !state.number1.contains(".")
            && state.number1.isNotBlank())
        {
            state = state.copy(
                number1 = state.number1 + "."
            )
            return
        }
        if (!state.number2.contains(".") && state.number2.isNotBlank())
        {
            state = state.copy(
                number2 = state.number2 + "."
            )
        }
    }

    private fun performPercent() {
        if (state.operation == null && !state.number1.contains("%")
            && state.number1.isNotBlank())
        {
            state = state.copy(
                number1 = (state.number1.toDouble() / 100).toString()
            )
            prettier()
            return
        }
        if (!state.number2.contains("%") && state.number2.isNotBlank())
        {
            state = state.copy(
                number2 = (state.number2.toDouble() / 100).toString()
            )
            prettier()
        }
    }

    private fun prettier() {
        if (state.operation == null && state.number1.endsWith(".0")
            && state.number1.isNotBlank())
        {
            state = state.copy(
                number1 = state.number1.dropLast(2)
            )
            return
        }
        if (state.number2.endsWith(".0") && state.number2.isNotBlank())
        {
            state = state.copy(
                number2 = state.number2.dropLast(2)
            )
        }
    }

    private fun enterNumber(number: String) {
        if (state.operation == null){
            if (state.number1.length >= MAX_NUMBER_LENGTH){
                return
            }
            if (state.number1 == "0") {
                state = state.copy(
                    number1 = ""
                )
            }
            state = state.copy(
                number1 = state.number1 + number
            )
            return
        }
        if (state.number2.length >= MAX_NUMBER_LENGTH){
            return
        }
        if (state.number2 == "0") {
            state = state.copy(
                number2 = ""
            )
        }
        state = state.copy(
            number2 = state.number2 + number
        )
    }

    companion object {
        private const val MAX_NUMBER_LENGTH = 13
    }
}
