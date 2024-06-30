package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository

class DeleteUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserData) {
        userRepository.deleteUser(user)
    }
}