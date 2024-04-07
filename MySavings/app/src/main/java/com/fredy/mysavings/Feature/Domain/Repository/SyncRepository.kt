package com.fredy.mysavings.Feature.Domain.Repository

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SyncRepository {
    suspend fun syncAccounts(withDelete: Boolean = true)
    suspend fun syncRecords(withDelete: Boolean = true)
    suspend fun syncCategory(withDelete: Boolean = true)
    suspend fun syncAll(withDelete: Boolean = true)
}

class SyncRepositoryImpl @Inject constructor(
    private val accountDataSource: AccountDataSource,
    private val accountDao: AccountDao,
    private val categoryDataSource: CategoryDataSource,
    private val categoryDao: CategoryDao,
    private val recordDataSource: RecordDataSource,
    private val recordDao: RecordDao,
    private val firebaseAuth: FirebaseAuth
) : SyncRepository {
    override suspend fun syncAccounts(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = if (currentUser.isNotNull()) currentUser.uid else ""
                val accounts = accountDataSource.getUserAccounts(userId).first()
                if (withDelete) {
                    accountDao.deleteAllAccounts()
                }
                accountDao.upsertAllAccountItem(accounts)
            }
        }
    }

    override suspend fun syncRecords(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = if (currentUser.isNotNull()) currentUser.uid else ""
                val records = recordDataSource.getUserRecords(userId)
                if (withDelete) {
                    recordDao.deleteAllRecords()
                }
                recordDao.upsertAllRecordItem(records)
            }
        }
    }

    override suspend fun syncCategory(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = if (currentUser.isNotNull()) currentUser.uid else ""
                val categories = categoryDataSource.getUserCategoriesOrderedByName(userId).first()
                if (withDelete) {
                    categoryDao.deleteAllCategories()
                }
                categoryDao.upsertAllCategoryItem(categories)
            }
        }
    }

    override suspend fun syncAll(withDelete: Boolean) {
        syncRecords(withDelete)
        syncAccounts(withDelete)
        syncCategory(withDelete)
    }

}