package com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource

import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Model.Currency

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
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
    suspend fun getCurrencyByCode(code: String,userId:String): Currency
    suspend fun getCurrencies(userId:String): Flow<List<Currency>>
}
class CurrencyDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
): CurrencyDataSource {
    private val currencyCollection = firestore.collection(
        "currency"
    )
    override suspend fun upsertCurrency(
        currency: Currency
    ) {
        currencyCollection.document(
            currency.currencyId
        ).set(
            currency
        )
    }

    override suspend fun upsertAllCurrencyItem(currencies: List<Currency>) {
        val batch = firestore.batch()
        for (currency in currencies) {
            batch.set(currencyCollection.document(currency.currencyId), currency)
        }
        batch.commit()
    }


    override suspend fun deleteCurrency(currency: Currency) {
        currencyCollection.document(currency.currencyId).delete()
    }

    override suspend fun getCurrencyByCode(code: String,userId:String): Currency {
        return try {
            currencyCollection.whereEqualTo("code", code).whereEqualTo(
                "userIdFk", userId
            ).get().await().toObjects<Currency>().firstOrNull()?: throw Exception(
                "Currency Not Found"
            )
        } catch (e: Exception) {
            Log.e("Failed to get currency: ${e.message}")
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
                Log.e("Failed to get currencies: ${e.message}")
                throw e
            }
        }
    }
}