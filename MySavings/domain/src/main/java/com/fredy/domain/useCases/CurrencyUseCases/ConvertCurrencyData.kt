package com.fredy.domain.useCases.CurrencyUseCases

import com.fredy.domain.model.Rate
import com.fredy.domain.modelUI.BalanceItem
import com.fredy.domain.repository.CurrencyRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ConvertCurrencyData(
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): BalanceItem {
        Timber.i(
            "convert: $amount$fromCurrency\nto: $toCurrency"
        )
        val tempFromCurrency = if (fromCurrency.contains(
                "None", ignoreCase = true
            )
        ) toCurrency else fromCurrency

        return try {
            val rates = currencyRepository.getRateResponse()?.rates

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
            Timber.e(
                "Failed to convert currency: $e"
            )
            throw e
        }
    }


    private fun singleBaseCurrencyConverter(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        rates: List<Rate>?
    ): Double {
        val toBaseRate = rates.getValueFromCode(toCurrency)
        val fromBaseRate = rates.getValueFromCode(fromCurrency)
        return amount * (toBaseRate / fromBaseRate)
    }

}