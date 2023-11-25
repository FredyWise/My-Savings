package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.RoomDatabase.Entity.UserData
import com.fredy.mysavings.Data.RoomDatabase.SavingsDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepository {
    suspend fun upsertUser(user: UserData)
    suspend fun deleteUser(user: UserData)
    fun getUser(userId: String): Flow<UserData>
    fun getAllUsersOrderedByName(): Flow<List<UserData>>
    fun searchUsers(usernameEmail: String): Flow<List<UserData>>
}

class UserRepositoryImpl @Inject constructor(
    private val savingsDatabase: SavingsDatabase
): UserRepository {
    override suspend fun upsertUser(user: UserData) {
        Firebase.firestore
            .collection("user")
            .document(user.firebaseUserId)
            .set(user)
        savingsDatabase.userDao.upsertUser(user)
    }

    override suspend fun deleteUser(user: UserData) {
        Firebase.firestore
            .collection("user")
            .document(user.firebaseUserId)
            .delete()

        savingsDatabase.userDao.deleteUser(user)
    }

    override fun getUser(userId: String): Flow<UserData> {
        return savingsDatabase.userDao.getUser(
            userId
        )
    }

    override fun getAllUsersOrderedByName(): Flow<List<UserData>> {
        return savingsDatabase.userDao.getAllUsersOrderedByName()
    }

    override fun searchUsers(usernameEmail: String): Flow<List<UserData>> {
        return savingsDatabase.userDao.searchUsers(
            usernameEmail
        )
    }
}