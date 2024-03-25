package com.fredy.mysavings.Data.Repository


import android.util.Log
import com.fredy.mysavings.Data.CSV.CSVDao
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.deletedAccount
import com.fredy.mysavings.Util.deletedCategory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


interface CSVRepository {
    suspend fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    )

    suspend fun inputFromCSV(
        directory: String,
        filename: String = "",
        delimiter: String = ","
    )
}

class CSVRepositoryImpl(
    private val csvDao: CSVDao,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val recordRepository: RecordRepository,
    private val categoryRepository: CategoryRepository,
) : CSVRepository {
    override suspend fun inputFromCSV(
        directory: String,
        filename: String,
        delimiter: String
    ) {
        withContext(Dispatchers.IO) {
            val trueRecords = csvDao.inputFromCSV(directory, filename, delimiter)
            val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
            trueRecords.forEach {
                Log.e(TAG, "inputFromCSV: $it")
                val accountIdFromFk = findAccountId(it.fromAccount, currentUserId)
                val accountIdToFk = findAccountId(it.toAccount, currentUserId)
                val categoryIdFk = findCategoryId(it.toCategory, currentUserId)
                val record = it.constructRecordsWithIds(
                    accountIdFromFk = accountIdFromFk,
                    accountIdToFk = accountIdToFk,
                    categoryIdFk = categoryIdFk,
                    userIdFk = currentUserId
                )
                Log.e(TAG, "inputFromCSV: $record")
                recordRepository.upsertRecordItem(record)
            }
        }
    }

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

    private fun TrueRecord.constructRecordsWithIds(
        accountIdFromFk: String,
        accountIdToFk: String,
        categoryIdFk: String,
        userIdFk: String,
    ): Record {
        return this.record.copy(
            accountIdFromFk = accountIdFromFk,
            accountIdToFk = accountIdToFk,
            categoryIdFk = categoryIdFk,
            userIdFk = userIdFk
        )
    }
    private fun Record.compareTo(record: Record): Boolean {
        return this.recordAmount == record.recordAmount &&
                this.recordCurrency == record.recordCurrency &&
                this.recordDateTime == record.recordDateTime
    }

    private suspend fun findAccountId(account: Account, userId: String): String {
        val accounts = getUserAccounts(userId)
        val accountId = if (!account.compareTo(deletedAccount)) {
            accounts.firstOrNull {
                it.compareTo(account)
            }?.accountId ?: accountRepository.upsertAccount(account.copy(userIdFk = userId))
        } else {
            deletedAccount.accountId
        }
        return accountId
    }

    private fun Account.compareTo(account: Account): Boolean {
        return this.accountName == account.accountName &&
                this.accountCurrency == account.accountCurrency &&
                this.accountIconDescription == account.accountIconDescription
    }

    private suspend fun findCategoryId(category: Category, userId: String): String {
        val categories = getUserCategories(userId)
        val categoryId = if (!category.compareTo(deletedCategory)) {
            categories.firstOrNull {
                it.compareTo(category)
            }?.categoryId ?: categoryRepository.upsertCategory(category.copy(userIdFk = userId))
        } else {
            deletedCategory.categoryId
        }
        return categoryId
    }

    private fun Category.compareTo(category: Category): Boolean {
        return this.categoryName == category.categoryName &&
                this.categoryIconDescription == category.categoryIconDescription
    }

    private suspend fun getUserAccounts(
        userId: String
    ) = Firebase.firestore.collection(
        "account"
    ).whereEqualTo(
        "userIdFk", userId
    ).get().await().toObjects<Account>()

    private suspend fun getUserCategories(
        userId: String
    ) = Firebase.firestore.collection(
        "category"
    ).whereEqualTo(
        "userIdFk", userId
    ).get().await().toObjects<Category>()
}
