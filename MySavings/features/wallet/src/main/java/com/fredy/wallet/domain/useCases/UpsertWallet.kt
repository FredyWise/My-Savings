package com.fredy.wallet.domain.useCases

import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.UserRepository
import com.fredy.wallet.domain.WalletRepository


class UpsertWallet(
    private val repository: WalletRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(wallet: Wallet): String {
        val currentUser = userRepository.getCurrentUser()!!
        val currentUserId = if (currentUser != null) currentUser.firebaseUserId else ""
        return repository.upsertWallet(wallet.copy(userIdFk = currentUserId))
    }
}