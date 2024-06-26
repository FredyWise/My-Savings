package com.fredy.mysavings.Data.APIs.CurrencyModels

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)