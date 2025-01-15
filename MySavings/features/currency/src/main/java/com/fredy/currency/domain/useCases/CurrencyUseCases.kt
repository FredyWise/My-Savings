package com.fredy.currency.domain.useCases

import com.fredy.domain.model.Record


data class CurrencyUseCases(
    val updateCurrency: UpdateCurrency,
    val getCurrencyRates: GetCurrencyRates,
    val convertCurrencyData: ConvertCurrencyData,
    val getCurrencies: GetCurrencies,
)
suspend fun CurrencyUseCases.getTotalRecordBalance(
    records: List<Record>,
    userCurrency: String
): Double {
    return records.sumOf { record ->
        this.currencyConverter(
            record.recordAmount,
            record.recordCurrency,
            userCurrency
        )
    }
}
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





