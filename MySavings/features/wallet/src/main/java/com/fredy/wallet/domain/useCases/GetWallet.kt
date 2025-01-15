package com.fredy.wallet.domain.useCases

import com.fredy.domain.model.Wallet
import com.fredy.wallet.domain.WalletRepository

import kotlinx.coroutines.flow.Flow

class GetWallet(
    private val repository: WalletRepository
) {
    operator fun invoke(accountId: String): Flow<Wallet> {
        return repository.getWallet(accountId)
    }
}