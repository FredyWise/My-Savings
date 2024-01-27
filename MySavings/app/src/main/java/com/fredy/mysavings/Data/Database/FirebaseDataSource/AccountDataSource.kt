package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Repository.CurrencyRepository
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.TAG
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject

interface AccountDataSource {
    suspend fun upsertAccountItem(account: Account)
    suspend fun deleteAccountItem(account: Account)
    suspend fun getAccount(accountId: String): Account
    suspend fun getUserAccounts(userId: String): List<Account>
    suspend fun getUserAvailableCurrency(userId: String): List<String>
}


class AccountDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
): AccountDataSource {
    private val accountCollection = firestore.collection(
        "account"
    )
    override suspend fun upsertAccountItem(//make sure the account already have uid // make sure to create the account id outside instead
        account: Account
    ) {
        accountCollection.document(
            account.accountId
        ).set(
            account
        )
    }

    override suspend fun deleteAccountItem(account: Account) {
        accountCollection.document(account.accountId).delete()
    }

    override suspend fun getAccount(accountId: String): Account {
        return try {
            accountCollection.document(accountId).get().await().toObject<Account>()?: throw Exception(
                "Account Not Found"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get account: ${e.message}")
            throw e
        }
    }

    override suspend fun getUserAccounts(userId: String): List<Account> {
        return try {
            val querySnapshot = accountCollection
                .whereEqualTo("userIdFk", userId)
                .get()
                .await()

           querySnapshot.toObjects()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user accounts: $e")
            throw e
        }
    }

    override suspend fun getUserAvailableCurrency(userId: String): List<String> {
        return try {
            val querySnapshot = accountCollection
                .whereEqualTo("userIdFk", userId)
                .get()
                .await()

            querySnapshot.toObjects<Account>().map { it.accountCurrency }.distinct()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get available currencies: $e")
            throw e
        }
    }

}