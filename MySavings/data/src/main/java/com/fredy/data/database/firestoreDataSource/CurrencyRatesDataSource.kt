package com.fredy.data.database.firestoreDataSource


import com.fredy.data.database.dto.FirebaseRatesCache
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CurrencyRatesDataSource {
    suspend fun upsertCurrencyRates(cache: FirebaseRatesCache)
    suspend fun deleteCurrencyRates(cache: FirebaseRatesCache)
    suspend fun getCurrencyRates(cacheId: String): FirebaseRatesCache?
}

class CurrencyRatesDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CurrencyRatesDataSource {
    private val currencyRatesCollection = firestore.collection(
        "currencyRates"
    )

    override suspend fun upsertCurrencyRates(
        rates: FirebaseRatesCache
    ) {
        val ratesCache = rates
        currencyRatesCollection.document(
            ratesCache.cacheId
        ).set(
            ratesCache
        )
    }

    override suspend fun deleteCurrencyRates(cache: FirebaseRatesCache) {
        currencyRatesCollection.document(cache.cacheId).delete()
    }

    override suspend fun getCurrencyRates(cacheId: String): FirebaseRatesCache? {
        return currencyRatesCollection.document(cacheId).get().await()
            .toObject<FirebaseRatesCache>()
    }
}