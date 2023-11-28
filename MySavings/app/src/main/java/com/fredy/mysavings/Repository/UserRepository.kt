package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.Database.Entity.UserData
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface UserRepository {
    suspend fun upsertUser(user: UserData)
    suspend fun deleteUser(user: UserData)
    fun getUser(userId: String): Flow<UserData>
    fun getAllUsersOrderedByName(): Flow<List<UserData>>
    fun searchUsers(usernameEmail: String): Flow<List<UserData>>
}

class UserRepositoryImpl(): UserRepository {

    override suspend fun upsertUser(user: UserData) {
        Firebase.firestore.collection("user").document(
            user.firebaseUserId
        ).set(user)
    }

    override suspend fun deleteUser(user: UserData) {
        Firebase.firestore.collection("user").document(
            user.firebaseUserId
        ).delete()
    }

    override fun getUser(userId: String): Flow<UserData> {
        return flow {
            var data = UserData()
            val result = Firebase.firestore.collection(
                "user"
            ).document(
                userId
            ).get().await()
            if (result.exists()) {
                data = result.toObject<UserData>()!!
            }
            emit(data)
        }
    }

    override fun getAllUsersOrderedByName() = callbackFlow<List<UserData>> {
        val listener = Firebase.firestore.collection(
            "user"
        ).orderBy(
            "username", Query.Direction.ASCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let {
                val data = it.documents.map { document ->
                    document.toObject<UserData>()!!
                }
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun searchUsers(usernameEmail: String) = callbackFlow<List<UserData>> {
        val listener = Firebase.firestore.collection(
            "user"
        ).where(
            Filter.arrayContains(
                "username", usernameEmail
            )
        ).where(
            Filter.arrayContains(
                "email", usernameEmail
            )
        ).orderBy(
            "username", Query.Direction.ASCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let {
                val data = it.documents.map { document ->
                    document.toObject<UserData>()!!
                }
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }
}