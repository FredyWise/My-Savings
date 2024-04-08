package com.fredy.mysavings.Feature.Domain.Repository

import androidx.compose.runtime.mutableStateOf
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Feature.Data.Database.Model.Currency
import com.fredy.mysavings.Feature.Data.Database.Model.RatesCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCurrencyRepository : CurrencyRepository {

    private val currencies = mutableListOf<Currency>()
    private var rates = mutableStateOf(RatesCache())

    override suspend fun updateRates(cache: RatesCache) {
        rates.value = cache
    }

    override suspend fun getRateResponse(base: String): RatesCache {
        return rates.value
    }

    override suspend fun updateCurrency(currency: Currency) {
        val existingCurrency = currencies.find { it.currencyId == currency.currencyId }
        if (existingCurrency != null) {
            currencies.remove(existingCurrency)
        }
        currencies.add(currency)
    }

    override suspend fun updateCurrencies(currencies: List<Currency>) {
        this.currencies.clear()
        this.currencies.addAll(currencies)
    }

    override suspend fun getCurrencies(userId: String): Flow<List<Currency>> {
        return flow { emit(currencies.filter { it.userIdFk == userId }) }
    }

    override suspend fun getInfo(): List<CurrencyInfoItem> {
        // Implement this method according to your needs
        return emptyList()
    }
}


