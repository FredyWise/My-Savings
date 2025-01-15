package com.fredy.currency.domain.useCases


import com.fredy.currency.domain.CurrencyRepository
import com.fredy.domain.model.Currency
import com.fredy.domain.model.Rate

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
        response?.let {
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

    private fun List<Rate>.updateRatesUsingCode(code: String, value: Double): List<Rate> {
        val index = this.indexOfFirst { it.code == code }
        val updatedList = this.toMutableList()
        updatedList[index] = updatedList[index].copy(value = value)
        return updatedList
    }
}