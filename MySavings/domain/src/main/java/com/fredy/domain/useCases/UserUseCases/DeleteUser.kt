package com.fredy.domain.useCases.UserUseCases

import com.fredy.domain.model.UserData
import com.fredy.domain.repository.UserRepository

class DeleteUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserData) {
        userRepository.deleteUser(user)
    }
}