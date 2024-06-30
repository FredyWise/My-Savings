package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository

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