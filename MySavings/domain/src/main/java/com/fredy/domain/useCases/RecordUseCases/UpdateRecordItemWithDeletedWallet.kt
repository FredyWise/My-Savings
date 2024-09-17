package com.fredy.domain.useCases.RecordUseCases

import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.DefaultData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class UpdateRecordItemWithDeletedWallet(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(wallet: Wallet) {
        withContext(Dispatchers.IO) {
            val currentUser = userRepository.getCurrentUser()!!
            val userId = currentUser?.firebaseUserId ?: ""
            val records = recordRepository.getUserRecords(userId).first()
            val tempRecords = records.filter {
                it.walletIdFromFk == wallet.walletId || it.walletIdToFk == wallet.walletId
            }.map {
                var record = it
                if (it.walletIdFromFk == wallet.walletId) {
                    record =
                        record.copy(walletIdFromFk = DefaultData.deletedWallet.walletId + userId)
                }
                if (it.walletIdToFk == wallet.walletId) {
                    record = record.copy(walletIdToFk = DefaultData.deletedWallet.walletId + userId)
                }
                record
            }
            recordRepository.upsertAllRecordItems(tempRecords)
        }
    }
}