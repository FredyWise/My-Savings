package com.fredy.data.repositoryImplement

import android.content.Context
import com.fredy.data.database.dao.BookDao
import com.fredy.data.database.dao.CategoryDao
import com.fredy.data.database.dao.RecordDao
import com.fredy.data.database.dao.WalletDao
import com.fredy.data.database.firestoreDataSource.BookDataSource
import com.fredy.data.database.firestoreDataSource.CategoryDataSource
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.data.database.firestoreDataSource.WalletDataSource
import com.fredy.data.util.DefaultData
import com.fredy.data.util.isInternetConnected
import com.fredy.domain.repository.SyncRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

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
            Timber.i("syncBooks: Start")
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = currentUser.uid
                try {
                    val books = bookDataSource.getUserBooksOrderedByName(userId).firstOrNull()
                    if (books.isNullOrEmpty()) {
                        throw (Exception("book is null or empty"))
                    }
                    if (withDelete) {
                        bookDao.deleteAllBooks()
                    }
                    bookDao.upsertAllBookItem(books)
                } catch (e: Exception) {
                    val tempBook = DefaultData.defaultBook.copy(
                        bookId = userId,
                        userIdFk = userId
                    )
                    bookDataSource.upsertBookItem(
                        tempBook
                    )
                }
            }
            Timber.i("syncBooks: Finish")
        }
    }

    override suspend fun syncAccounts(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            Timber.i("syncAccounts: Start")
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = currentUser.uid
                walletDataSource.upsertWalletItem(
                    DefaultData.deletedWallet.copy(
                        walletId = DefaultData.deletedWallet.walletId + userId,
                        userIdFk = userId
                    )
                )
                val accounts = walletDataSource.getUserWallets(userId).first()
                if (withDelete) {
                    walletDao.deleteAllWallets()
                }
                walletDao.upsertAllWalletItem(accounts)
            }
            Timber.i("syncAccounts: Finish")
        }
    }

    override suspend fun syncRecords(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            Timber.i("syncRecords: Start")
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = currentUser.uid
                val records = recordDataSource.getUserRecords(userId).first()
                if (withDelete) {
                    recordDao.deleteAllRecords()
                }
                recordDao.upsertAllRecordItem(records)
            }
            Timber.i("syncRecords: Finish")
        }
    }

    override suspend fun syncCategory(withDelete: Boolean) {
        withContext(Dispatchers.IO) {
            Timber.i("syncCategory: Start")
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val userId = currentUser.uid
                categoryDataSource.upsertCategoryItem(
                    DefaultData.deletedCategory.copy(
                        categoryId = DefaultData.deletedCategory.categoryId + userId,
                        userIdFk = userId
                    )
                )
                categoryDataSource.upsertCategoryItem(
                    DefaultData.transferCategory.copy(
                        categoryId = DefaultData.transferCategory.categoryId + userId,
                        userIdFk = userId
                    )
                )
                val categories = categoryDataSource.getUserCategoriesOrderedByName(userId).first()
                if (withDelete) {
                    categoryDao.deleteAllCategories()
                }
                categoryDao.upsertAllCategoryItem(categories)
            }
            Timber.i("syncCategory: Finish")
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