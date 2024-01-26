package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Util.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AccountRepository {
    suspend fun upsertAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    fun getAccount(accountId: String): Flow<Account>
    fun getUserAccountOrderedByName(): Flow<List<Account>>
    fun getUserAccountTotalBalance(): Flow<Double>
    fun getUserAvailableCurrency(): Flow<List<String>>
}


class AccountRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): AccountRepository {
    private val accountCollection = firestore.collection(
        "account"
    )
    override suspend fun upsertAccount(account: Account) {
        val currentUser = firebaseAuth.currentUser!!
        val documentReference = if (account.accountId.isEmpty()) {
            accountCollection.document()
        } else {
            accountCollection.document(account.accountId)
        }
        documentReference.set(
            account.copy(
                accountId = documentReference.id,
                userIdFk = currentUser.uid
            )
        )
    }

    override suspend fun deleteAccount(account: Account) {
        accountCollection.document(account.accountId).delete()
    }


    override fun getAccount(accountId: String): Flow<Account> {
        return flow {
            val result = accountCollection.document(
                accountId
            ).get().await().toObject<Account>() ?: Account()
            emit(result)
        }
    }

    override fun getUserAccountOrderedByName() = callbackFlow<List<Account>> {
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserAccountOrderedByName: " + currentUser!!.uid,

            )
        val listener = accountCollection.whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser.uid else ""
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let {
                val data = it.documents.map { document ->
                    document.toObject<Account>()!!
                }
                Log.i(
                    TAG,
                    "getUserAccountOrderedByName: " + data,

                    )
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserAccountTotalBalance() = callbackFlow<Double> {
        val currentUser = firebaseAuth.currentUser
        val listener = accountCollection.whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    TAG,
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                Log.i(
                    TAG,
                    "getUserAccountTotalBalance2: " + it.documents,
                )
                val data = it.sumOf { document ->
                    document.toObject<Account>().accountAmount
                }
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserAvailableCurrency() = callbackFlow<List<String>> {
        val currentUser = firebaseAuth.currentUser
        val listener = accountCollection.whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.i(
                    TAG,
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                Log.i(
                    TAG,
                    "getUserAccountTotalBalance2: " + it.documents,
                )
                val data = it.map { document ->
                    document.toObject<Account>().accountCurrency
                }.toMutableList()
                trySend(data.toList().distinct())
            }
        }

        awaitClose {
            listener.remove()
        }
    }
}