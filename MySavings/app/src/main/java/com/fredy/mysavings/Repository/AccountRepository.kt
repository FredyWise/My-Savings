package com.fredy.mysavings.Repository

import android.util.Log
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Util.TAG
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface AccountRepository {
    suspend fun upsertAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    fun getAccount(accountId: String): Flow<Account>
    fun getUserAccountOrderedByName(): Flow<List<Account>>
    fun getUserAccountTotalBalance(): Flow<Double>
    fun getUserAvailableCurrency(): Flow<List<String>>
}


class AccountRepositoryImpl(): AccountRepository {

    override suspend fun upsertAccount(account: Account) {
        val currentUser = Firebase.auth.currentUser
        val accountCollection = Firebase.firestore.collection(
            "account"
        )
        if (account.accountId.isEmpty()) {
            accountCollection.add(
                account
            ).addOnSuccessListener { document ->
                accountCollection.document(
                    document.id
                ).set(
                    account.copy(
                        accountId = document.id,
                        userIdFk = currentUser!!.uid
                    )
                )
            }
        } else {
            accountCollection.document(
                account.accountId
            ).set(
                account.copy(
                    userIdFk = currentUser!!.uid
                )
            )
        }
    }

    override suspend fun deleteAccount(account: Account) {
        Firebase.firestore.collection("account").document(
            account.accountId
        ).delete()
    }

    override fun getAccount(accountId: String): Flow<Account> {
        return flow {
            val result = Firebase.firestore.collection(
                "account"
            ).document(
                accountId
            ).get().await().toObject<Account>()!!
            emit(result)
        }
    }

    override fun getUserAccountOrderedByName() = callbackFlow<List<Account>> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserAccountOrderedByName: " + currentUser!!.uid,

            )
        val listener = Firebase.firestore.collection(
            "account"
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let {
                val data = it.documents.map { document ->
                    document.toObject<Account>()!!
                }
                Log.e(
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
        val currentUser = Firebase.auth.currentUser
        val listener = Firebase.firestore.collection(
            "account"
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                Log.e(
                    "BABI",
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
        val currentUser = Firebase.auth.currentUser
        val listener = Firebase.firestore.collection(
            "account"
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                Log.e(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.documents,
                )
                val data = it.map { document ->
                    document.toObject<Account>().accountCurrency
                }.toMutableList()
                data.add(0, "None")
                trySend(data.toList())
            }
        }

        awaitClose {
            listener.remove()
        }
    }
}