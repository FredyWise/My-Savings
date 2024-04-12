package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

data class UserUseCases(
    val insertUser: InsertUser,
    val updateUser: UpdateUser,
    val deleteUser: DeleteUser,
    val getUser: GetUser,
    val getCurrentUser: GetCurrentUser,
    val getAllUsersOrderedByName: GetAllUsersOrderedByName,
    val searchUsers: SearchUsers
)


class InsertUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserData) {
        val isExist = userRepository.getUser(user.firebaseUserId).firstOrNull()
        Log.i("InsertUser.Data: $isExist")
        if (!isExist.isNotNull()){
            userRepository.upsertUser(user)
        }
    }
}

class UpdateUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserData) {
        userRepository.upsertUser(user)
    }
}

class DeleteUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserData) {
        userRepository.deleteUser(user)
    }
}

class GetUser(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<UserData?> {
        return userRepository.getUser(userId)
    }
}

class GetCurrentUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Resource<UserData?>> {
        return userRepository.getCurrentUser()
    }
}

class GetAllUsersOrderedByName(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<List<UserData>> {
        return userRepository.getAllUsersOrderedByName()
    }
}

class SearchUsers(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(usernameEmail: String): Flow<List<UserData>> {
        return userRepository.searchUsers(usernameEmail)
    }
}
