package com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource

import com.fredy.mysavings.Feature.Data.Database.Model.FirebaseRatesCache
import com.fredy.mysavings.Feature.Data.Database.Model.RatesCache
import com.fredy.mysavings.Feature.Mappers.toFireBaseRatesCache
import com.fredy.mysavings.Feature.Mappers.toRatesCache
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
        currency: RatesCache
    ) {
        currencyRatesCollection.document(
            currency.cacheId
        ).set(
            currency.toFireBaseRatesCache()
        )
    }

    override suspend fun deleteCurrencyRates(currency: RatesCache) {
        currencyRatesCollection.document(currency.cacheId).delete()
    }

    override suspend fun getCurrencyRates(base: String): RatesCache? {
        return currencyRatesCollection.document(base).get().await().toObject<FirebaseRatesCache>()?.toRatesCache()

    }
}