package com.fredy.mysavings.Feature.Data.RepositoryImpl

import com.fredy.mysavings.Feature.Data.CSV.CSVDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Util.DefaultData
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CSVRepositoryImpl(
    private val csvDao: CSVDao,
    private val walletRepository: WalletRepository,
    private val recordDataSource: RecordDataSource,
    private val categoryRepository: CategoryRepository,
) : CSVRepository {

    override suspend fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String
    ) {
        withContext(Dispatchers.IO) {
            csvDao.outputToCSV(directory, filename, trueRecords, delimiter)
        }
    }

    override suspend fun inputFromCSV(
        currentUserId:String,
        directory: String,
        delimiter: String,
        book: Book
    ): List<TrueRecord> {
        return withContext(Dispatchers.IO) {
            val trueRecords = csvDao.inputFromCSV(directory, delimiter)
            val trueRecord = trueRecords.map {
                Log.e("inputFromCSVRepo: $it")
                val accountIdFromFk = findAccountId(it.fromWallet, currentUserId)
                val accountIdToFk = findAccountId(it.toWallet, currentUserId)
                val categoryIdFk = findCategoryId(it.toCategory, currentUserId)
                val recordId = findRecordId(it.record, currentUserId)
                it.constructRecordsWithIds(
                    recordId = recordId,
                    accountIdFromFk = accountIdFromFk,
                    accountIdToFk = accountIdToFk,
                    categoryIdFk = categoryIdFk,
                    userIdFk = currentUserId,
                    bookIdFk = book.bookId
                )
            }
            Log.e("inputFromCSVRepo1: $trueRecord")
            trueRecord
        }
    }



    private fun TrueRecord.constructRecordsWithIds(
        recordId: String,
        accountIdFromFk: String,
        accountIdToFk: String,
        categoryIdFk: String,
        userIdFk: String,
        bookIdFk:String,
    ): TrueRecord {
        return this.copy(
            record = record.copy(
                recordId = recordId,
                walletIdFromFk = accountIdFromFk,
                walletIdToFk = accountIdToFk,
                categoryIdFk = categoryIdFk,
                userIdFk = userIdFk,
                bookIdFk = bookIdFk
            )
        )
    }

    private suspend fun findRecordId(record: Record, userId: String): String {
        val records = recordDataSource.getUserRecords(userId).first()
        return records.singleOrNull { it.compareTo(record) }?.recordId ?: ""
    }


    private fun Record.compareTo(record: Record): Boolean {
        return recordAmount == record.recordAmount &&
                recordCurrency == record.recordCurrency &&
                recordDateTime == record.recordDateTime &&
                recordNotes == record.recordNotes &&
                recordType == record.recordType
    }

    private suspend fun findAccountId(wallet: Wallet, userId: String): String {
        val accounts = walletRepository.getUserWallets(userId).first()
        val accountId = if (wallet.walletId != DefaultData.deletedWallet.walletId + userId) {
            accounts.firstOrNull {
                it.compareTo(wallet)
            }?.walletId ?: walletRepository.upsertWallet(wallet.copy(userIdFk = userId))
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
            }?.categoryId ?: categoryRepository.upsertCategory(category.copy(userIdFk = userId))
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