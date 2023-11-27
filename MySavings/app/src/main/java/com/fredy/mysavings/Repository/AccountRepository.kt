package com.fredy.mysavings.Repository

import android.util.Log
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.GoogleAuth.GoogleAuthUiClient
import com.fredy.mysavings.Util.TAG
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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
}


class AccountRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleAuthUiClient: GoogleAuthUiClient,
): AccountRepository {
    val currentUser = googleAuthUiClient.getSignedInUser(
        firebaseAuth
    )

    override suspend fun upsertAccount(account: Account) {
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
                        userIdFk = currentUser!!.firebaseUserId
                    )
                )
            }
        } else {
            accountCollection.document(
                account.accountId
            ).set(
                account.copy(
                    userIdFk = currentUser!!.firebaseUserId
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
        val listener = Firebase.firestore.collection(
            "account"
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.firebaseUserId
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
        val listener = Firebase.firestore.collection(
            "account"
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.firebaseUserId
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
}