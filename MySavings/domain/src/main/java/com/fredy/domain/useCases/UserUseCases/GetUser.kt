package com.fredy.domain.useCases.UserUseCases

import com.fredy.domain.model.UserData
import com.fredy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUser(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<UserData?> {
        return userRepository.getUser(userId)
    }
}