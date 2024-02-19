package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Util.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AccountDataSource {
    suspend fun upsertAccountItem(account: Account)
    suspend fun deleteAccountItem(account: Account)
    suspend fun getAccount(accountId: String): Account
    suspend fun getUserAccounts(userId: String): Flow<List<Account>>
}


class AccountDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : AccountDataSource {
    private val accountCollection = firestore.collection(
        "account"
    )

    override suspend fun upsertAccountItem(
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
            accountCollection.document(accountId).get().await().toObject<Account>()
                ?: throw Exception(
                    "Account Not Found"
                )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get account: ${e.message}")
            throw e
        }
    }

    override suspend fun getUserAccounts(userId: String): Flow<List<Account>> {
        return try {
            val querySnapshot = accountCollection
                .whereEqualTo("userIdFk", userId)
                .snapshots()

            querySnapshot.map { it.toObjects()}
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user accounts: $e")
            throw e
        }
    }

}