package com.fredy.domain.useCases.CurrencyUseCases

import com.fredy.domain.model.Currency
import com.fredy.domain.model.Rate

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

fun List<Rate>?.getValueFromCode(code: String): Double {
    return this?.find { it.code == code }?.value?: throw IllegalArgumentException(
        "Currency '$code' not found in rates."
    )
}

fun List<Currency>.changeBase(newBaseCode: String): List<Currency> {
    val newBase = this.first { it.code == newBaseCode }
    return this.map { currency ->
        currency.copy(value = currency.value / newBase.value)
    }
}


