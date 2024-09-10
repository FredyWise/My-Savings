package com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases

import com.fredy.domain.useCases.CurrencyUseCases.ConvertCurrencyData
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.GetCurrencies
import com.fredy.domain.useCases.CurrencyUseCases.GetCurrencyRates
import com.fredy.domain.useCases.CurrencyUseCases.UpdateCurrency

data class CurrencyUseCases(
    val updateCurrency: UpdateCurrency,
    val getCurrencyRates: GetCurrencyRates,
    val convertCurrencyData: ConvertCurrencyData,
    val getCurrencies: GetCurrencies,
)

suspend fun CurrencyUseCases.currencyConverter(
    amount: Double, from: String, to: String
): Double {
    return if (from != to && from.isNotEmpty() && to.isNotEmpty()) {
        this.convertCurrencyData(
            amount, from, to
        ).amount
    } else {
        amount
    }
}


