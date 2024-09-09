package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.domain.util.mappers.getCurrencies
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
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

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