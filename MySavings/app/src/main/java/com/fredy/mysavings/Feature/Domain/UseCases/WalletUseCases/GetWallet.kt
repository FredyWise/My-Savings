package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import kotlinx.coroutines.flow.Flow

class GetWallet(
    private val repository: WalletRepository
) {
    operator fun invoke(accountId: String): Flow<Wallet> {
        return repository.getWallet(accountId)
    }
}