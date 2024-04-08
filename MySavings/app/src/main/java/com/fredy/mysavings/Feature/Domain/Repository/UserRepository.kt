package com.fredy.mysavings.Feature.Domain.Repository

import android.util.Log
import com.fredy.mysavings.Feature.Data.Database.Dao.UserDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.UserDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.DefaultData.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface UserRepository {
    suspend fun upsertUser(user: UserData)
    suspend fun deleteUser(user: UserData)
    fun getUser(userId: String): Flow<UserData>
    suspend fun getCurrentUser(): Flow<Resource<UserData?>>
    suspend fun getAllUsersOrderedByName(): Flow<List<UserData>>
    suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>>
}

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDataSource: UserDataSource,
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun upsertUser(user: UserData) {
        withContext(Dispatchers.IO) {
            userDataSource.upsertUser(user)
            userDao.upsertUser(user)
        }
    }

    override suspend fun deleteUser(user: UserData) {
        withContext(Dispatchers.IO) {
            userDataSource.deleteUser(user)
            userDao.deleteUser(user)
        }
    }

    override fun getUser(userId: String): Flow<UserData> {
        return flow {
            val user = withContext(Dispatchers.IO) {
                userDao.getUser(userId)
            }
            emit(user)
        }
    }

    override suspend fun getCurrentUser(): Flow<Resource<UserData?>> = flow {
        emit(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val user = withContext(Dispatchers.IO) {
                userDataSource.getUser(currentUser.uid)
            }
            emit(Resource.Success(user))
        }
    }.catch { e ->
        Log.i(
            TAG,
            "getCurrentUser.Error: $e"
        )
        emit(Resource.Error(e.message.toString()))
    }

    override suspend fun getAllUsersOrderedByName(): Flow<List<UserData>> {
        return userDataSource.getAllUsersOrderedByName()
    }

    override suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>> {
        return userDataSource.searchUsers(usernameEmail)
    }

}