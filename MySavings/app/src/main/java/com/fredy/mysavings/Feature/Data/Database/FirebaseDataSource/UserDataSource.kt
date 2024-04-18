package com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource

import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Model.UserData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
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

interface UserDataSource {
    suspend fun upsertUser(user: UserData)
    suspend fun deleteUser(user: UserData)
    suspend fun getUser(userId: String): UserData?
    suspend fun getAllUsersOrderedByName(): Flow<List<UserData>>
    suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>>
}

class UserDataSourceImpl(
    private val firestore: FirebaseFirestore,
) : UserDataSource {

    private val userCollection = firestore.collection("user")

    override suspend fun upsertUser(user: UserData) {
        withContext(Dispatchers.IO) {
            userCollection.document(user.firebaseUserId).set(user)
        }
    }

    override suspend fun deleteUser(user: UserData) {
        withContext(Dispatchers.IO) {
            userCollection.document(user.firebaseUserId).delete()
        }
    }

    override suspend fun getUser(userId: String): UserData? {
        return withContext(Dispatchers.IO) {
            try {
                userCollection.document(userId).get().await().toObject<UserData>()
            } catch (e: Exception) {
                Log.e(
                    "Failed to get user: ${e.message}"
                )
                throw e
            }
        }

    }

    override suspend fun getAllUsersOrderedByName(): Flow<List<UserData>> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = userCollection.orderBy(
                    "username", Query.Direction.ASCENDING
                ).snapshots()

                querySnapshot.map { it.toObjects<UserData>() }
            } catch (e: Exception) {
                Log.e(
                    "Failed to get all user: $e"
                )
                throw e
            }
        }

    }

    override suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = userCollection.where(
                    Filter.arrayContains(
                        "username", usernameEmail
                    )
                ).where(
                    Filter.arrayContains(
                        "email", usernameEmail
                    )
                ).orderBy(
                    "username", Query.Direction.ASCENDING
                ).snapshots()

                querySnapshot.map { it.toObjects<UserData>() }
            } catch (e: Exception) {
                Log.e(
                    "Failed to get all user: $e"
                )
                throw e
            }

        }
    }
}
