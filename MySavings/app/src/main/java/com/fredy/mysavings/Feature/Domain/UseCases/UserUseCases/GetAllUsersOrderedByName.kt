package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetAllUsersOrderedByName(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<List<UserData>> {
        return userRepository.getAllUsersOrderedByName()
    }
}