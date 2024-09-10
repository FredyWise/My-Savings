package com.fredy.domain.repository


import com.fredy.core.credentials.ApiCredentials
import com.fredy.domain.model.Currency
import com.fredy.domain.model.Rate
import com.fredy.domain.model.RatesCache
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun updateRates(cache: RatesCache)
    suspend fun updateCurrency(currency: Currency)
    suspend fun updateCurrencies(currencies: List<Currency>)
    suspend fun getCurrencies(userId: String): Flow<List<Currency>>
    suspend fun getRateResponse(
        base: String = ApiCredentials.CurrencyModels.BASE_CURRENCY
    ): RatesCache

    suspend fun getInfo(): List<com.fredy.data.api.countryModels.countryDTO.CurrencyInfoItem>?
}


