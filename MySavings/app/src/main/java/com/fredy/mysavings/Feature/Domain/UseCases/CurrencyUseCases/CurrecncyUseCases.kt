package com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases

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


