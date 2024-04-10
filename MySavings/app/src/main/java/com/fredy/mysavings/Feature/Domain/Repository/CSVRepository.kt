package com.fredy.mysavings.Feature.Domain.Repository


import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Data.CSV.CSVDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord

import com.fredy.mysavings.Util.DefaultData.deletedAccount
import com.fredy.mysavings.Util.DefaultData.deletedCategory
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.DBInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


interface CSVRepository {
    suspend fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    )

    suspend fun inputFromCSV(
        currentUserId: String,
        directory: String,
        delimiter: String = ","
    ): List<TrueRecord>
}

class CSVRepositoryImpl(
    private val csvDao: CSVDao,
    private val accountRepository: AccountRepository,
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
        delimiter: String
    ): List<TrueRecord> {
        return withContext(Dispatchers.IO) {
            val trueRecords = csvDao.inputFromCSV(directory, delimiter)
            val trueRecord = trueRecords.map {
                Log.e("inputFromCSVRepo: $it")
                val accountIdFromFk = findAccountId(it.fromAccount, currentUserId)
                val accountIdToFk = findAccountId(it.toAccount, currentUserId)
                val categoryIdFk = findCategoryId(it.toCategory, currentUserId)
                val recordId = findRecordId(it.record, currentUserId)
                it.constructRecordsWithIds(
                    recordId = recordId,
                    accountIdFromFk = accountIdFromFk,
                    accountIdToFk = accountIdToFk,
                    categoryIdFk = categoryIdFk,
                    userIdFk = currentUserId
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
    ): TrueRecord {
        return this.copy(
            record = record.copy(
                recordId = recordId,
                accountIdFromFk = accountIdFromFk,
                accountIdToFk = accountIdToFk,
                categoryIdFk = categoryIdFk,
                userIdFk = userIdFk
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

    private suspend fun findAccountId(account: Account, userId: String): String {
        val accounts = accountRepository.getUserAccounts(userId).first()
        val accountId = if (account.accountId != deletedAccount.accountId + userId) {
            accounts.firstOrNull {
                it.compareTo(account)
            }?.accountId ?: accountRepository.upsertAccount(account.copy(userIdFk = userId))
        } else {
            deletedAccount.accountId + userId
        }
        return accountId
    }

    private fun Account.compareTo(account: Account): Boolean {
        return accountName == account.accountName &&
                accountCurrency == account.accountCurrency &&
                accountIconDescription == account.accountIconDescription
    }

    private suspend fun findCategoryId(category: Category, userId: String): String {
        val categories = categoryRepository.getUserCategories(userId).first()
        val categoryId = if (category.categoryId != deletedCategory.categoryId + userId) {
            categories.firstOrNull {
                it.compareTo(category)
            }?.categoryId ?: categoryRepository.upsertCategory(category.copy(userIdFk = userId))
        } else {
            deletedCategory.categoryId + userId
        }
        return categoryId
    }

    private fun Category.compareTo(category: Category): Boolean {
        return categoryName == category.categoryName &&
                categoryIconDescription == category.categoryIconDescription
    }

}
