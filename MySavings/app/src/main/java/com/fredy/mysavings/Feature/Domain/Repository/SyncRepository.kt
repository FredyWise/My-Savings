package com.fredy.mysavings.Feature.Domain.Repository

import android.content.Context
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Feature.Data.Database.Dao.BookDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.BookDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Util.DefaultData.defaultBook
import com.fredy.mysavings.Util.DefaultData.deletedAccount
import com.fredy.mysavings.Util.DefaultData.deletedCategory
import com.fredy.mysavings.Util.DefaultData.transferCategory
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.isInternetConnected
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SyncRepository {
    suspend fun syncBooks(withDelete: Boolean = true)
    suspend fun syncAccounts(withDelete: Boolean = true)
    suspend fun syncRecords(withDelete: Boolean = true)
    suspend fun syncCategory(withDelete: Boolean = true)
    suspend fun syncAll(withDelete: Boolean = true)
}

class SyncRepositoryImpl @Inject constructor(
    private val context: Context,
    private val accountDataSource: AccountDataSource,
    private val accountDao: AccountDao,
    private val categoryDataSource: CategoryDataSource,
    private val categoryDao: CategoryDao,
    private val bookDataSource: BookDataSource,
    private val bookDao: BookDao,
    private val recordDataSource: RecordDataSource,
    private val recordDao: RecordDao,
    private val firebaseAuth: FirebaseAuth
) : SyncRepository {
    override suspend fun syncBooks(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = if (currentUser.isNotNull()) currentUser.uid else ""
                try {
                    val books = bookDataSource.getUserBooksOrderedByName(userId)
                    if (withDelete) {
                        bookDao.deleteAllBooks()
                    }
                    bookDao.upsertAllBookItem(books.first())
                }catch (e:Exception){
                    val bookCollection = FirebaseFirestore.getInstance().collection(
                        "book"
                    )
                    val newBookRef = bookCollection.document()
                    val tempBook = defaultBook.copy(
                        bookId = newBookRef.id,
                        userIdFk = userId
                    )
                    bookDataSource.upsertBookItem(
                        tempBook
                    )
                }
            }
        }
    }

    override suspend fun syncAccounts(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = if (currentUser.isNotNull()) currentUser.uid else ""
                accountDataSource.upsertAccountItem(
                    deletedAccount.copy(
                        accountId = deletedAccount.accountId + userId,
                        userIdFk = userId
                    )
                )
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
                val records = recordDataSource.getUserRecords(userId).first()
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
                categoryDataSource.upsertCategoryItem(
                    deletedCategory.copy(
                        categoryId = deletedCategory.categoryId + userId,
                        userIdFk = userId
                    )
                )
                categoryDataSource.upsertCategoryItem(
                    transferCategory.copy(
                        categoryId = transferCategory.categoryId + userId,
                        userIdFk = userId
                    )
                )
                val categories = categoryDataSource.getUserCategoriesOrderedByName(userId).first()
                if (withDelete) {
                    categoryDao.deleteAllCategories()
                }
                categoryDao.upsertAllCategoryItem(categories)
            }
        }
    }

    override suspend fun syncAll(withDelete: Boolean) {
        if (isInternetConnected(context)) {
            syncBooks(withDelete)
            syncRecords(withDelete)
            syncAccounts(withDelete)
            syncCategory(withDelete)
        }
    }

}