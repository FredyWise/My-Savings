package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.WalletRepository

class DeleteWallet(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(wallet: Wallet) {
        repository.deleteWallet(wallet)
    }
}