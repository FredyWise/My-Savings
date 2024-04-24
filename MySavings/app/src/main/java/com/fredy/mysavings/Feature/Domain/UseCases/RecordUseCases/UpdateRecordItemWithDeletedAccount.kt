package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class UpdateRecordItemWithDeletedAccount(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(wallet: Wallet) {
        withContext(Dispatchers.IO) {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
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