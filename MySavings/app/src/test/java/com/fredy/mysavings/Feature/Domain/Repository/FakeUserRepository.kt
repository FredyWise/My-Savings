package com.fredy.mysavings.Feature.Domain.Repository

import android.content.Context
import android.net.Uri
import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeUserRepository : UserRepository {

    private val users = mutableListOf<UserData>()

    override suspend fun upsertUser(user: UserData) {
        val existingUser = users.find { it.firebaseUserId == user.firebaseUserId }
        if (existingUser != null) {
            users.remove(existingUser)
            users.add(user)
            user.firebaseUserId
        } else {
            user.firebaseUserId.also { users.add(user) }
        }
    }

    override suspend fun deleteUser(user: UserData) {
        users.remove(user)
    }

    override fun getUser(firebaseUserId: String): Flow<UserData?> {
        return flow { emit(users.find { it.firebaseUserId == firebaseUserId }) }
    }

    override suspend fun getCurrentUserFlow(): Flow<Resource<UserData?>> {
        return flow { emit(Resource.Success(users.firstOrNull())) }
    }

    override suspend fun getCurrentUser(): UserData? {
        return users.firstOrNull()
    }

    override suspend fun getAllUsersOrderedByName(): Flow<List<UserData>> {
        return flow { emit(users.sortedBy { it.username }) }
    }

    override suspend fun searchUsers(usernameEmail: String): Flow<List<UserData>> {
        return flow { emit(users.filter { it.username?.contains(usernameEmail)?:false || it.email?.contains(usernameEmail)?:false }) }
    }
}

