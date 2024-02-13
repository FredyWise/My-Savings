package com.fredy.mysavings.Data.Repository

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Data.Database.FirebaseDataSource.RecordDataSource
import com.google.firebase.auth.FirebaseAuth

interface SyncRepository {
    suspend fun syncAccounts()
    suspend fun syncRecords()
    suspend fun syncCategory()
}

class SyncRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val accountDao: AccountDao,
    private val categoryDataSource: CategoryDataSource,
    private val categoryDao: CategoryDao,
    private val recordDataSource: RecordDataSource,
    private val recordDao: RecordDao,
    private val firebaseAuth: FirebaseAuth
):SyncRepository{
    override suspend fun syncAccounts() {
        val currentUser = firebaseAuth.currentUser!!
        val userId = if (currentUser.isNotNull()) currentUser.uid else ""
        val accounts = accountDataSource.getUserAccounts(userId)
        accounts.forEach {
            accountDao.upsertAccountItem(it)
        }
    }

    override suspend fun syncRecords() {
        val currentUser = firebaseAuth.currentUser!!
        val userId = if (currentUser.isNotNull()) currentUser.uid else ""
        val records = recordDataSource.getUserRecords(userId)
        records.forEach {
            recordDao.upsertRecordItem(it)
        }
    }

    override suspend fun syncCategory() {
        val currentUser = firebaseAuth.currentUser!!
        val userId = if (currentUser.isNotNull()) currentUser.uid else ""
        val categories = categoryDataSource.getUserCategoriesOrderedByName(userId)
        categories.forEach {
            categoryDao.upsertCategoryItem(it)
        }
    }

}