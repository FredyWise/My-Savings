package com.fredy.wallet.domain.useCases

import com.fredy.domain.model.Wallet
import com.fredy.wallet.domain.WalletRepository


class DeleteWallet(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(wallet: Wallet) {
        repository.deleteWallet(wallet)
    }
}