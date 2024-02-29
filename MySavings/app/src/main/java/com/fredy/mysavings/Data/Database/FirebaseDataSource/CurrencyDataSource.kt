package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Util.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CurrencyDataSource {
    suspend fun upsertCurrency(cache: Currency)
    suspend fun deleteCurrency(cache: Currency)
    suspend fun getCurrency(cacheId: String): Currency
    suspend fun getCurrencies(): List<Currency>
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
            currency.name
        ).set(
            currency
        )
    }

    override suspend fun deleteCurrency(currency: Currency) {
        currencyCollection.document(currency.name).delete()
    }

    override suspend fun getCurrency(name: String): Currency {
        return try {
            currencyCollection.document(name).get().await().toObject<Currency>()?: throw Exception(
                "Currency Not Found"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get currency: ${e.message}")
            throw e
        }
    }

    override suspend fun getCurrencies(): List<Currency> {
        return try {
            currencyCollection.get().await().toObjects<Currency>()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get currencies: ${e.message}")
            throw e
        }
    }
}