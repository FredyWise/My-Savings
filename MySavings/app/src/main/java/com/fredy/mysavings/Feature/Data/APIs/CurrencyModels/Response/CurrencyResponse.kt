package com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)