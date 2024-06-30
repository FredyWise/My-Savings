package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository

class DeleteWallet(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(wallet: Wallet) {
        repository.deleteWallet(wallet)
    }
}