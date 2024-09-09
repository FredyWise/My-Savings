package com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases

import com.fredy.data.api.currencyModels.currencyDTO.Rates
import com.fredy.domain.repository.CurrencyRepository
import com.fredy.mysavings.Feature.Presentation.Util.BalanceItem
import com.fredy.mysavings.Util.Log
import com.fredy.domain.util.mappers.getRateForCurrency
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
        val toBaseRate = com.fredy.domain.util.mappers.getRateForCurrency(
            toCurrency
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$toCurrency' not found in rates."
        )
        val fromBaseRate = com.fredy.domain.util.mappers.getRateForCurrency(
            fromCurrency
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$fromCurrency' not found in rates."
        )
        return amount * (toBaseRate / fromBaseRate)
    }
}