package com.fredy.mysavings.Data.Add

sealed class CalcAction {

    data class Number (val number: String): CalcAction()
    object Clear: CalcAction()
    object Delete: CalcAction()
    object DecimalPoint: CalcAction()
    object Percent: CalcAction()
    object Calculate: CalcAction()
    data class Operation(val operation: CalcOperation): CalcAction()
}

sealed class CalcOperation (val symbol: String) {

    object Add: CalcOperation("+")
    object Substract: CalcOperation("-")
    object Multiply: CalcOperation("x")
    object Divide: CalcOperation("/")
}

data class CalcState(
    val number1: String = "0",
    val number2: String = "",
    val operation: CalcOperation? = null
)