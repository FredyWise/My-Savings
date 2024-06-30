package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SearchUsers(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(usernameEmail: String): Flow<List<UserData>> {
        return userRepository.searchUsers(usernameEmail)
    }
}