package com.fredy.currency.data.currencyModels.currencyDTO


data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)