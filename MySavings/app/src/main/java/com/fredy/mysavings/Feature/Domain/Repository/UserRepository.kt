package com.fredy.mysavings.Feature.Domain.Repository

import android.util.Log
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface UserRepository {
    suspend fun insertUser(user: UserData)
    suspend fun upsertUser(user: UserData)
    suspend fun deleteUser(user: UserData)
    fun getUser(userId: String): Flow<UserData>
    suspend fun getCurrentUser(): Flow<Resource<UserData?>>
    fun getAllUsersOrderedByName(): Flow<List<UserData>>
    fun searchUsers(usernameEmail: String): Flow<List<UserData>>
}

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
    private val userCollection = firestore.collection(
        "user"
    )

    override suspend fun insertUser(user: UserData) {
        withContext(Dispatchers.IO) {
            val document = userCollection.document(user.firebaseUserId)
            val snapshot = document.get().await()
            if (!snapshot.exists()) {
                document.set(user)
            }
        }
    }

    override suspend fun upsertUser(user: UserData) {
        withContext(Dispatchers.IO) {
            userCollection.document(
                user.firebaseUserId
            ).set(user)
        }
    }

    override suspend fun deleteUser(user: UserData) {
        withContext(Dispatchers.IO) {
            userCollection.document(
                user.firebaseUserId
            ).delete()
        }
    }

    override fun getUser(userId: String): Flow<UserData> {
        return flow {
            var data = UserData()
            val result = withContext(Dispatchers.IO) {
                userCollection.document(
                    userId
                ).get().await()
            }
            if (result.exists()) {
                data = result.toObject<UserData>()!!
            }
            emit(data)
        }
    }

    override suspend fun getCurrentUser(): Flow<Resource<UserData?>> = flow {
        emit(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userDocument = withContext(Dispatchers.IO) {
                userCollection.document(currentUser.uid).get().await()
            }
            emit(Resource.Success(userDocument.toObject<UserData>()))
        }
    }.catch { e ->
        Log.i(
            TAG,
            "getCurrentUser.Error: $e"
        )
        emit(Resource.Error(e.message.toString()))
    }

    override fun getAllUsersOrderedByName() = callbackFlow<List<UserData>> {
        val listener = userCollection.orderBy(
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
        val listener = userCollection.where(
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