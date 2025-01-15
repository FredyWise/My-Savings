package com.fredy.domain.userUseCases

import com.fredy.domain.model.UserData
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import kotlinx.coroutines.flow.Flow


class GetCurrentUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Resource<UserData?, DataError.Database>> {
        return userRepository.getCurrentUserFlow()
    }
}