package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel

sealed interface CalcEvent {

    data class Number (val number: String): CalcEvent
    object Clear: CalcEvent
    object Delete: CalcEvent
    object DecimalPoint: CalcEvent
    object Percent: CalcEvent
    object Calculate: CalcEvent
    data class Operation(val operation: CalcOperation): CalcEvent
}

sealed class CalcOperation (val symbol: String) {

    object Add: CalcOperation("+")
    object Substract: CalcOperation("-")
    object Multiply: CalcOperation("x")
    object Divide: CalcOperation("/")
}

