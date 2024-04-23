package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import kotlinx.coroutines.flow.Flow

class GetCurrentUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Resource<UserData?>> {
        return userRepository.getCurrentUser()
    }
}