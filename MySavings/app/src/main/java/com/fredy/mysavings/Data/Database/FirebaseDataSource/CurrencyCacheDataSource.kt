package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.Database.Model.RatesCache
import com.fredy.mysavings.Util.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CurrencyCacheDataSource {
    suspend fun upsertCurrencyCache(cache: RatesCache)
    suspend fun deleteCurrencyCache(cache: RatesCache)
    suspend fun getCurrencyCache(cacheId: String): RatesCache
}

class CurrencyCacheDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CurrencyCacheDataSource {
    private val currencyCollection = firestore.collection(
        "currencyCache"
    )

    override suspend fun upsertCurrencyCache(
        currency: RatesCache
    ) {
        currencyCollection.document(
            currency.base
        ).set(
            currency
        )
    }

    override suspend fun deleteCurrencyCache(currency: RatesCache) {
        currencyCollection.document(currency.base).delete()
    }

    override suspend fun getCurrencyCache(base: String): RatesCache {
        return try {
            currencyCollection.document(base).get().await().toObject<RatesCache>()
                ?: throw Exception(
                    "CurrencyCache Not Found"
                )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get currencyCache: ${e.message}")
            throw e
        }
    }
}