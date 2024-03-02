package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Mappers.toTrueRecords
import com.fredy.mysavings.Util.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CurrencyDataSource {
    suspend fun upsertCurrency(cache: Currency)
    suspend fun upsertAllCurrencyItem(currencies: List<Currency>)
    suspend fun deleteCurrency(cache: Currency)
    suspend fun getCurrency(cacheId: String): Currency
    suspend fun getCurrencies(userId:String): Flow<List<Currency>>
}
class CurrencyDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
):CurrencyDataSource{
    private val currencyCollection = firestore.collection(
        "Currency"
    )
    override suspend fun upsertCurrency(
        currency: Currency
    ) {
        currencyCollection.document(
            currency.code
        ).set(
            currency
        )
    }

    override suspend fun upsertAllCurrencyItem(currencies: List<Currency>) {
        val batch = firestore.batch()
        for (currency in currencies) {
            batch.set(currencyCollection.document(currency.code), currency)
        }
        batch.commit()
    }


    override suspend fun deleteCurrency(currency: Currency) {
        currencyCollection.document(currency.code).delete()
    }

    override suspend fun getCurrency(code: String): Currency {
        return try {
            currencyCollection.document(code).get().await().toObject<Currency>()?: throw Exception(
                "Currency Not Found"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get currency: ${e.message}")
            throw e
        }
    }

    override suspend fun getCurrencies(userId:String): Flow<List<Currency>> {
        return withContext(Dispatchers.IO) {
            try {
                currencyCollection.whereEqualTo(
                    "userIdFk", userId
                ).snapshots().map { it.toObjects() }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get currencies: ${e.message}")
                throw e
            }
        }
    }
}