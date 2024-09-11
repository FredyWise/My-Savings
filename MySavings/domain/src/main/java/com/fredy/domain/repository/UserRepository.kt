package com.fredy.domain.repository

import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource
import com.fredy.domain.model.UserData
import com.google.firebase.auth.FirebaseUser

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun upsertUser(user: UserData)
    suspend fun deleteUser(user: UserData)
    fun getUser(userId: String): Flow<UserData?>
    suspend fun getCurrentUserFlow(): Flow<Resource<UserData?, DataError.Database>>
    suspend fun getCurrentUser(): UserData?
    suspend fun getAllUsersOrderedByName(): Flow<List<UserData>>
    suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>>
}

