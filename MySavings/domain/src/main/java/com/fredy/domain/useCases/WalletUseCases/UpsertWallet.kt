package com.fredy.domain.useCases.WalletUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository

class UpsertWallet(
    private val repository: WalletRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(wallet: Wallet): String {
        val currentUser = userRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertWallet(wallet.copy(userIdFk = currentUserId))
    }
}