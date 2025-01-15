package com.fredy.domain.userUseCases

import com.fredy.domain.model.UserData
import com.fredy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetAllUsersOrderedByName(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<List<UserData>> {
        return userRepository.getAllUsersOrderedByName()
    }
}