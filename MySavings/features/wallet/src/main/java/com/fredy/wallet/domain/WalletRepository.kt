package com.fredy.wallet.domain

import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Record
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    suspend fun upsertWallet(wallet: Wallet): String
    suspend fun deleteWallet(wallet: Wallet)
    fun getWallet(walletId: String): Flow<Wallet>
    fun getUserWallets(userId: String): Flow<List<Wallet>>
    fun getUserRecordsByType(userId: String, recordType: RecordType): Flow<List<Record>>
    fun getUserWalletRecordsOrderedByDateTime(
        userId: String,
        walletId: String,
        sortType: SortType,
    ): Flow<List<TrueRecord>>
}


