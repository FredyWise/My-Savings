package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository

class UpsertWallet(
    private val repository: WalletRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(wallet: Wallet): String {
        val currentUser = authRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertWallet(wallet.copy(userIdFk = currentUserId))
    }
}