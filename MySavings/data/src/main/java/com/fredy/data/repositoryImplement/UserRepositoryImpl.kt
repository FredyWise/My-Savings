package com.fredy.data.repositoryImplement

import com.fredy.data.database.dao.UserDao
import com.fredy.data.database.firestoreDataSource.UserDataSource
import com.fredy.data.mappers.toDataUser
import com.fredy.data.mappers.toDomainUser
import com.fredy.domain.model.UserData
import com.fredy.domain.repository.UserRepository
import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDataSource: UserDataSource,
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun upsertUser(user: UserData) {
        withContext(Dispatchers.IO) {
            userDataSource.upsertUser(user.toDataUser())
            userDao.upsertUser(user.toDataUser())
        }
    }

    override suspend fun deleteUser(user: UserData) {
        withContext(Dispatchers.IO) {
            userDataSource.deleteUser(user.toDataUser())
            userDao.deleteUser(user.toDataUser())
        }
    }

    override fun getUser(userId: String): Flow<UserData?> {
        return flow {
            val user = withContext(Dispatchers.IO) {
                userDataSource.getUser(userId)
            }
            emit(user?.toDomainUser())
        }
    }

    override suspend fun getCurrentUserFlow(): Flow<Resource<UserData?, DataError.Database>> =
        flow<Resource<UserData?, DataError.Database>>   {
            emit(Resource.Loading())
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val user = withContext(Dispatchers.IO) {
                    userDataSource.getUser(currentUser.uid)
                }?.toDomainUser()
                emit(Resource.Success(user))
            }
        }.catch { e ->
            Timber.i(
                "getCurrentUser.Error: $e"
            )
            emit(Resource.Error(DataError.Database.UNKNOWN))
        }

    override suspend fun getCurrentUser() =
        userDataSource.getUser(firebaseAuth.currentUser?.uid ?: "-1")?.toDomainUser()

    override suspend fun getAllUsersOrderedByName(): Flow<List<UserData>> {
        return userDataSource.getAllUsersOrderedByName().map { it.toDomainUser() }
    }

    override suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>> {
        return userDataSource.searchUsers(usernameEmail).map { it.toDomainUser() }
    }

}