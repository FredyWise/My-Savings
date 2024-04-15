package com.fredy.mysavings.Feature.Data.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Feature.Data.Database.Model.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Upsert
    suspend fun upsertWalletItem(wallet: Wallet)

    @Upsert
    suspend fun upsertAllWalletItem(wallets: List<Wallet>)

    @Delete
    suspend fun deleteWalletItem(wallet: Wallet)

    @Query("DELETE FROM wallet")
    suspend fun deleteAllWallets()

    @Query("SELECT * FROM wallet WHERE walletId = :walletId")
    suspend fun getWallet(walletId: String): Wallet

    @Query("SELECT * FROM wallet WHERE userIdFk = :userId")
    fun getUserWallets(userId: String): Flow<List<Wallet>>

    @Query("SELECT DISTINCT walletCurrency FROM wallet WHERE userIdFk = :userId")
    fun getUserAvailableCurrency(userId: String): Flow<List<String>>

}