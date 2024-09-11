package com.fredy.domain.repository

import com.fredy.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    suspend fun upsertWallet(wallet: Wallet): String
    suspend fun deleteWallet(wallet: Wallet)
    fun getWallet(walletId: String): Flow<Wallet>
    fun getUserWallets(userId: String): Flow<List<Wallet>>
}


