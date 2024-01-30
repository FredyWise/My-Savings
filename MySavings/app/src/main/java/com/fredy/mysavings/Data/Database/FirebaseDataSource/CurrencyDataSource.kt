package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.Database.Model.CurrencyCache
import com.fredy.mysavings.Util.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CurrencyDataSource {
    suspend fun upsertCurrencyCacheItem(cache: CurrencyCache)
    suspend fun deleteCurrencyCacheItem(cache: CurrencyCache)
    suspend fun getCurrencyCache(cacheId: String): CurrencyCache
}
class CurrencyDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
):CurrencyDataSource{
    private val currencyCacheCollection = firestore.collection(
        "currencyCache"
    )
    override suspend fun upsertCurrencyCacheItem(//make sure the currencyCache already have uid // make sure to create the currencyCache id outside instead
        currencyCache: CurrencyCache
    ) {
        currencyCacheCollection.document(
            currencyCache.base
        ).set(
            currencyCache
        )
    }

    override suspend fun deleteCurrencyCacheItem(currencyCache: CurrencyCache) {
        currencyCacheCollection.document(currencyCache.base).delete()
    }

    override suspend fun getCurrencyCache(currencyCacheId: String): CurrencyCache {
        return try {
            currencyCacheCollection.document(currencyCacheId).get().await().toObject<CurrencyCache>()?: throw Exception(
                "CurrencyCache Not Found"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get currencyCache: ${e.message}")
            throw e
        }
    }
}