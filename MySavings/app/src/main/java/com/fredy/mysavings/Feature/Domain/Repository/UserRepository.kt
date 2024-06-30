package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Util.Resource

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun upsertUser(user: UserData)
    suspend fun deleteUser(user: UserData)
    fun getUser(userId: String): Flow<UserData?>
    suspend fun getCurrentUserFlow(): Flow<Resource<UserData?>>
    suspend fun getCurrentUser(): UserData?
    suspend fun getAllUsersOrderedByName(): Flow<List<UserData>>
    suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>>
}

