package com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases

import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.first

class InputFromCSV(
    private val csvRepository: CSVRepository,
    private val authRepository: AuthRepository,
    private val recordRepository: RecordRepository,
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(
        directory: String,
        delimiter: String = ",",
        book: Book
    ): List<TrueRecord> {
        val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
        val trueRecords = csvRepository.inputFromCSV(currentUserId, directory, delimiter).map {
            Log.i("inputFromCSV: $it")
            val walletIdFromFk = findWalletId(it.fromWallet, currentUserId)
            val walletIdToFk = findWalletId(it.toWallet, currentUserId)
            val categoryIdFk = findCategoryId(it.toCategory, currentUserId)
            val recordId = findRecordId(it.record, currentUserId)
            it.constructTrueRecordsWithIds(
                recordId = recordId,
                walletIdFromFk = walletIdFromFk,
                walletIdToFk = walletIdToFk,
                categoryIdFk = categoryIdFk,
                userIdFk = currentUserId,
                bookIdFk = book.bookId
            )
        }
        Log.i("inputFromCSV.Data: $trueRecords")
        return trueRecords
    }

    private fun TrueRecord.constructTrueRecordsWithIds(
        recordId: String,
        walletIdFromFk: String,
        walletIdToFk: String,
        categoryIdFk: String,
        userIdFk: String,
        bookIdFk:String,
    ): TrueRecord {
        return this.copy(
            record = record.copy(
                recordId = recordId,
                walletIdFromFk = walletIdFromFk,
                walletIdToFk = walletIdToFk,
                categoryIdFk = categoryIdFk,
                userIdFk = userIdFk,
                bookIdFk = bookIdFk
            ),
            toWallet = toWallet.copy(walletId = walletIdToFk,userIdFk = userIdFk),
            fromWallet = fromWallet.copy(walletId = walletIdFromFk, userIdFk = userIdFk),
            toCategory = toCategory.copy(categoryId = categoryIdFk, userIdFk = userIdFk)
        )
    }

    private suspend fun findRecordId(record: Record, userId: String): String {
        val records = recordRepository.getUserRecords(userId).first()
        return records.singleOrNull { it.compareTo(record) }?.recordId ?: ""
    }


    private fun Record.compareTo(record: Record): Boolean {
        return recordAmount == record.recordAmount &&
                recordCurrency == record.recordCurrency &&
                recordDateTime == record.recordDateTime &&
                recordNotes == record.recordNotes &&
                recordType == record.recordType
    }

    private suspend fun findWalletId(wallet: Wallet, userId: String): String {
        val accounts = walletRepository.getUserWallets(userId).first()
        val accountId = if (wallet.walletId != DefaultData.deletedWallet.walletId + userId) {
            accounts.firstOrNull {
                it.compareTo(wallet)
            }?.walletId ?: ""
        } else {
            DefaultData.deletedWallet.walletId + userId
        }
        return accountId
    }

    private fun Wallet.compareTo(wallet: Wallet): Boolean {
        return walletName == wallet.walletName &&
                walletCurrency == wallet.walletCurrency &&
                walletIconDescription == wallet.walletIconDescription
    }

    private suspend fun findCategoryId(category: Category, userId: String): String {
        val categories = categoryRepository.getUserCategories(userId).first()
        val categoryId = if (category.categoryId != DefaultData.deletedCategory.categoryId + userId) {
            categories.firstOrNull {
                it.compareTo(category)
            }?.categoryId ?: ""
        } else {
            DefaultData.deletedCategory.categoryId + userId
        }
        return categoryId
    }

    private fun Category.compareTo(category: Category): Boolean {
        return categoryName == category.categoryName &&
                categoryIconDescription == category.categoryIconDescription
    }
}