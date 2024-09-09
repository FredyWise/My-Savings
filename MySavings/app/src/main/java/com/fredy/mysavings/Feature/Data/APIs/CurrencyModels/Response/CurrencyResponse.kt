package com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response

import com.fredy.data.api.currencyModels.currencyDTO.Rates

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)