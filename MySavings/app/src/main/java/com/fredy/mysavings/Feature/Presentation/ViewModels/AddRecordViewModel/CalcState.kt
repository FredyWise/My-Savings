package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel

data class CalcState(
    val number1: String = "0",
    val number2: String = "",
    val operation: CalcOperation? = null
)