package com.fredy.domain.useCases.WalletUseCases

import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.mysavings.Feature.Domain.Util.Mappers.getCurrencies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetWalletsCurrencies(
    private val repository: WalletRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<String>> {
        return flow {
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId

                withContext(Dispatchers.IO) {
                    repository.getUserWallets(
                        userId
                    ).map { it.getCurrencies() }
                }.collect { data ->
                    emit(data)
                }
            }
        }
    }
}