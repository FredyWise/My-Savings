package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.Database.Model.CurrencyCache
import com.fredy.mysavings.Util.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CurrencyCacheDataSource {
    suspend fun upsertCurrencyCache(cache: CurrencyCache)
    suspend fun deleteCurrencyCache(cache: CurrencyCache)
    suspend fun getCurrencyCache(cacheId: String): CurrencyCache
}

class CurrencyCacheDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CurrencyCacheDataSource {
    private val currencyCollection = firestore.collection(
        ApiCredentials.CurrencyModels.BASE_CURRENCY
    )

    override suspend fun upsertCurrencyCache(
        currency: CurrencyCache
    ) {
        currencyCollection.document(
            currency.base
        ).set(
            currency
        )
    }

    override suspend fun deleteCurrencyCache(currency: CurrencyCache) {
        currencyCollection.document(currency.base).delete()
    }

    override suspend fun getCurrencyCache(base: String): CurrencyCache {
        return try {
            currencyCollection.document(base).get().await().toObject<CurrencyCache>()
                ?: throw Exception(
                    "CurrencyCache Not Found"
                )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get currencyCache: ${e.message}")
            throw e
        }
    }
}