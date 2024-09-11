package com.fredy.domain.useCases.WalletUseCases

import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow

class GetWallet(
    private val repository: WalletRepository
) {
    operator fun invoke(accountId: String): Flow<Wallet> {
        return repository.getWallet(accountId)
    }
}