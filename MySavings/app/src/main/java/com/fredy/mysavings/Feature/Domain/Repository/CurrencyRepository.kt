package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Feature.Domain.Model.Currency
import com.fredy.mysavings.Feature.Domain.Model.RatesCache
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun updateRates(cache: RatesCache)
    suspend fun updateCurrency(currency: Currency)
    suspend fun updateCurrencies(currencies: List<Currency>)
    suspend fun getCurrencies(userId: String): Flow<List<Currency>>
    suspend fun getRateResponse(
        base: String = ApiCredentials.CurrencyModels.BASE_CURRENCY
    ): RatesCache

    suspend fun getInfo(): List<CurrencyInfoItem>
}


