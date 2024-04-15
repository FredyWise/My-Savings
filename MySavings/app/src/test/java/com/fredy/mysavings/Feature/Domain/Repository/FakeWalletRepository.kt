package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Model.Wallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeWalletRepository : WalletRepository {

    private val wallets = mutableListOf<Wallet>()

    override suspend fun upsertWallet(wallet: Wallet): String {
        val existingAccount = wallets.find { it.walletId == wallet.walletId }
        return if (existingAccount != null) {
            wallets.remove(existingAccount)
            wallets.add(wallet)
            wallet.walletId
        } else {
            wallet.walletId.also { wallets.add(wallet) }
        }
    }

    override suspend fun deleteWallet(wallet: Wallet) {
        wallets.remove(wallet)
    }

    override fun getWallet(accountId: String): Flow<Wallet> {
        return flow { emit(wallets.find { it.walletId == accountId }!!) }
    }

    override fun getUserWallets(userId: String): Flow<List<Wallet>> {
        return flow { emit(wallets.filter { it.userIdFk == userId }) }
    }
}

