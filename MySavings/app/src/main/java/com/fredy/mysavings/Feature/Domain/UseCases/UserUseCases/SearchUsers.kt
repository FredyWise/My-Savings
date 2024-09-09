package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import com.fredy.domain.model.UserData
import com.fredy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SearchUsers(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(usernameEmail: String): Flow<List<UserData>> {
        return userRepository.searchUsers(usernameEmail)
    }
}