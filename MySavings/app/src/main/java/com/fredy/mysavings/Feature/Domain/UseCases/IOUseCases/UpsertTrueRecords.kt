package com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases

import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Util.Log

class UpsertTrueRecords(
    private val userRepository: UserRepository,
    private val recordRepository: RecordRepository,
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository

) {
    suspend operator fun invoke(
        trueRecords: List<TrueRecord>
    ) {
        val currentUserId = userRepository.getCurrentUser()!!.firebaseUserId
        trueRecords.forEach {
            Log.i("UpsertTrueRecords: $it")
            val walletIdFromFk = findWalletId(it.fromWallet.copy(userIdFk = currentUserId))
            val walletIdToFk = findWalletId(it.toWallet.copy(userIdFk = currentUserId))
            val categoryIdFk = findCategoryId(it.toCategory.copy(userIdFk = currentUserId))
            recordRepository.upsertRecordItem(it.record.copy(
                walletIdFromFk = walletIdFromFk,
                walletIdToFk = walletIdToFk,
                categoryIdFk = categoryIdFk,
                userIdFk = currentUserId
            ))
        }
    }

    private suspend fun findWalletId(wallet: Wallet): String {
        return walletRepository.upsertWallet(wallet)
    }

    private suspend fun findCategoryId(category: Category): String {
        return categoryRepository.upsertCategory(category)
    }
}