package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import com.fredy.domain.model.UserData
import com.fredy.domain.repository.UserRepository

class UpdateUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserData) {
        userRepository.upsertUser(user)
    }
}