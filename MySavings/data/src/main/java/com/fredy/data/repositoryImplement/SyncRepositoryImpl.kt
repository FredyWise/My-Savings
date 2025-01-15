package com.fredy.data.repositoryImplement

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.fredy.data.database.dao.BookDao
import com.fredy.data.database.dao.CategoryDao
import com.fredy.data.database.dao.RecordDao
import com.fredy.data.database.dao.WalletDao
import com.fredy.data.database.firestoreDataSource.BookDataSource
import com.fredy.data.database.firestoreDataSource.CategoryDataSource
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.data.database.firestoreDataSource.WalletDataSource
import com.fredy.data.mappers.toDataBook
import com.fredy.data.mappers.toDataCategory
import com.fredy.data.mappers.toDataWallet
import com.fredy.data.util.isInternetConnected
import com.fredy.domain.modelUI.FilterState
import com.fredy.domain.repository.SyncRepository
import com.fredy.domain.util.DefaultData.deletedCategory
import com.fredy.domain.util.DefaultData.deletedWallet
import com.fredy.domain.util.DefaultData.transferCategory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                    val dataBook = com.fredy.domain.util.DefaultData.defaultBook.copy(
                        bookId = userId,
                        userIdFk = userId
                    ).toDataBook()
                    bookDataSource.upsertBookItem(
                        dataBook
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
                    deletedWallet.copy(
                        walletId = deletedWallet.walletId + userId,
                        userIdFk = userId
                    ).toDataWallet()
                )
                val dataWallets = walletDataSource.getUserWallets(userId).first()
                if (withDelete) {
                    walletDao.deleteAllWallets()
                }
                walletDao.upsertAllWalletItem(dataWallets)
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
                    deletedCategory.copy(
                        categoryId = deletedCategory.categoryId + userId,
                        userIdFk = userId
                    ).toDataCategory()
                )
                categoryDataSource.upsertCategoryItem(
                    transferCategory.copy(
                        categoryId = transferCategory.categoryId + userId,
                        userIdFk = userId
                    ).toDataCategory()
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

    private val _filterSettings = MutableStateFlow(FilterState())
    override val filterSettings: StateFlow<FilterState> = _filterSettings.asStateFlow()


    override fun saveFilterSettings(filterState: FilterState) {
        _filterSettings.value = filterState
        Timber.i("saveFilterSettings: $filterState")
    }

//    override fun getFilterSettings(): StateFlow<FilterState> {
//        Timber.i("getFilterSettings: ${_filterSettings.value}")
//        return filterSettings
//    }

}