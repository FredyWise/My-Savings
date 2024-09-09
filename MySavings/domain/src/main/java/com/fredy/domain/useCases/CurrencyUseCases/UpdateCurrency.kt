package com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases

import com.fredy.domain.model.Currency
import com.fredy.domain.repository.CurrencyRepository
import com.fredy.domain.util.mappers.updateRatesUsingCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateCurrency(
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(currency: Currency) {
        withContext(Dispatchers.IO) {
            currencyRepository.updateCurrency(currency)
            syncRates(currency)
        }
    }

    private suspend fun syncRates(currency: Currency) {
        val response = currencyRepository.getRateResponse()
        val tempRates = response.copy(
            rates = response.rates.updateRatesUsingCode(
                currency.code,
                currency.value
            )
        )
        currencyRepository.updateRates(
            tempRates
        )
    }
}