package com.fredy.mysavings.Feature.Domain.Repository

import android.content.Context
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Dao.WalletDao
import com.fredy.mysavings.Feature.Data.Database.Dao.BookDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.WalletDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.BookDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Util.DefaultData.defaultBook
import com.fredy.mysavings.Util.DefaultData.deletedWallet
import com.fredy.mysavings.Util.DefaultData.deletedCategory
import com.fredy.mysavings.Util.DefaultData.transferCategory
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.isInternetConnected
import com.google.firebase.auth.FirebaseAuth
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
    private val walletDataSource: WalletDataSource,
    private val walletDao: WalletDao,
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
            Log.i("syncBooks: Start")
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = if (currentUser.isNotNull()) currentUser.uid else ""
                try {
                    val books = bookDataSource.getUserBooksOrderedByName(userId).firstOrNull()
                    if (books.isNullOrEmpty()){
                        throw(Exception("book is null or empty"))
                    }
                    if (withDelete) {
                        bookDao.deleteAllBooks()
                    }
                    bookDao.upsertAllBookItem(books)
                } catch (e: Exception) {
                    val tempBook = defaultBook.copy(
                        bookId = userId,
                        userIdFk = userId
                    )
                    bookDataSource.upsertBookItem(
                        tempBook
                    )
                }
            }
            Log.i("syncBooks: Finish")
        }
    }

    override suspend fun syncAccounts(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            Log.i("syncAccounts: Start")
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = if (currentUser.isNotNull()) currentUser.uid else ""
                walletDataSource.upsertWalletItem(
                    deletedWallet.copy(
                        walletId = deletedWallet.walletId + userId,
                        userIdFk = userId
                    )
                )
                val accounts = walletDataSource.getUserWallets(userId).first()
                if (withDelete) {
                    walletDao.deleteAllWallets()
                }
                walletDao.upsertAllWalletItem(accounts)
            }
            Log.i("syncAccounts: Finish")
        }
    }

    override suspend fun syncRecords(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            Log.i("syncRecords: Start")
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = if (currentUser.isNotNull()) currentUser.uid else ""
                val records = recordDataSource.getUserRecords(userId).first()
                if (withDelete) {
                    recordDao.deleteAllRecords()
                }
                recordDao.upsertAllRecordItem(records)
            }
            Log.i("syncRecords: Finish")
        }
    }

    override suspend fun syncCategory(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            Log.i("syncCategory: Start")
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
            Log.i("syncCategory: Finish")
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