package com.fredy.io.domain.useCases

import com.fredy.domain.model.Category
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import timber.log.Timber

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
            Timber.i("UpsertTrueRecords: $it")
            val walletIdFromFk = findWalletId(it.fromWallet.copy(userIdFk = currentUserId))
            val walletIdToFk = findWalletId(it.toWallet.copy(userIdFk = currentUserId))
            val categoryIdFk = findCategoryId(it.toCategory.copy(userIdFk = currentUserId))
            recordRepository.upsertRecordItem(
                it.record.copy(
                    walletIdFromFk = walletIdFromFk,
                    walletIdToFk = walletIdToFk,
                    categoryIdFk = categoryIdFk,
                    userIdFk = currentUserId
                )
            )
        }
    }

    private suspend fun findWalletId(wallet: Wallet): String {
        return walletRepository.upsertWallet(wallet)
    }

    private suspend fun findCategoryId(category: Category): String {
        return categoryRepository.upsertCategory(category)
    }
}