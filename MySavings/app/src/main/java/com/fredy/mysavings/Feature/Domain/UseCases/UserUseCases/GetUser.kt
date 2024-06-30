package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUser(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<UserData?> {
        return userRepository.getUser(userId)
    }
}