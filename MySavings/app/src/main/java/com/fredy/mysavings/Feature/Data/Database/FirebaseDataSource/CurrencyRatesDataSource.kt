package com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource

import com.fredy.mysavings.Feature.Domain.Model.FirebaseRatesCache
import com.fredy.mysavings.Feature.Domain.Model.RatesCache
import com.fredy.mysavings.Util.Mappers.toFireBaseRatesCache
import com.fredy.mysavings.Util.Mappers.toRatesCache
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CurrencyRatesDataSource {
    suspend fun upsertCurrencyRates(cache: RatesCache)
    suspend fun deleteCurrencyRates(cache: RatesCache)
    suspend fun getCurrencyRates(cacheId: String): RatesCache?
}

class CurrencyRatesDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CurrencyRatesDataSource {
    private val currencyRatesCollection = firestore.collection(
        "currencyRates"
    )

    override suspend fun upsertCurrencyRates(
        rates: RatesCache
    ) {
        val ratesCache =rates.toFireBaseRatesCache()
        currencyRatesCollection.document(
            ratesCache.cacheId
        ).set(
            ratesCache
        )
    }

    override suspend fun deleteCurrencyRates(cache: RatesCache) {
        currencyRatesCollection.document(cache.cacheId).delete()
    }

    override suspend fun getCurrencyRates(cacheId: String): RatesCache? {
        return currencyRatesCollection.document(cacheId).get().await().toObject<FirebaseRatesCache>()?.toRatesCache()

    }
}