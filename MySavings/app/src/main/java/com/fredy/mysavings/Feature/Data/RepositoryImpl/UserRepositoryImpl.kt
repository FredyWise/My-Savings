package com.fredy.mysavings.Feature.Data.RepositoryImpl

import com.fredy.data.database.dao.UserDao
import com.fredy.data.database.firestoreDataSource.UserDataSource
import com.fredy.domain.model.UserData
import com.fredy.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

    override fun getUser(userId: String): Flow<UserData?> {
        return flow {
            val user = withContext(Dispatchers.IO) {
                userDataSource.getUser(userId)
            }
            emit(user)
        }
    }

    override suspend fun getCurrentUserFlow(): Flow<Resource<UserData?>> = flow {
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
            "getCurrentUser.Error: $e"
        )
        emit(Resource.Error(e.message.toString()))
    }

    override suspend fun getCurrentUser() = userDataSource.getUser(firebaseAuth.currentUser?.uid ?: "-1")

    override suspend fun getAllUsersOrderedByName(): Flow<List<UserData>> {
        return userDataSource.getAllUsersOrderedByName()
    }

    override suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>> {
        return userDataSource.searchUsers(usernameEmail)
    }

}