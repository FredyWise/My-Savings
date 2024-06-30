package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Domain.Model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    suspend fun upsertWallet(wallet: Wallet): String
    suspend fun deleteWallet(wallet: Wallet)
    fun getWallet(accountId: String): Flow<Wallet>
    fun getUserWallets(userId: String): Flow<List<Wallet>>
}


