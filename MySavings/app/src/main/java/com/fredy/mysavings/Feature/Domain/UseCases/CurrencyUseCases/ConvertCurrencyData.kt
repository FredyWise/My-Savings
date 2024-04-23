package com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases

import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.Mappers.getRateForCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConvertCurrencyData(
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): BalanceItem {
        Log.i(
            "convert: $amount$fromCurrency\nto: $toCurrency"
        )
        val tempFromCurrency = if (fromCurrency.contains(
                "None", ignoreCase = true
            )
        ) toCurrency else fromCurrency

        return try {
            val rates = currencyRepository.getRateResponse().rates

            val result = withContext(Dispatchers.IO) {
                singleBaseCurrencyConverter(
                    amount,
                    tempFromCurrency,
                    toCurrency,
                    rates
                )
            }
            BalanceItem(
                amount = result,
                currency = toCurrency
            )
        } catch (e: Exception) {
            Log.e(
                "Failed to convert currency: $e"
            )
            throw e
        }
    }


    private fun singleBaseCurrencyConverter(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        rates: Rates
    ): Double {
        val toBaseRate = rates.getRateForCurrency(
            toCurrency
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$toCurrency' not found in rates."
        )
        val fromBaseRate = rates.getRateForCurrency(
            fromCurrency
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$fromCurrency' not found in rates."
        )
        return amount * (toBaseRate / fromBaseRate)
    }
}