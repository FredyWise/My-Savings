package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.deletedAccount
import com.fredy.mysavings.Util.deletedCategory
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.RecordMap
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject


interface RecordRepository {
    suspend fun upsertRecordItem(record: Record)
    suspend fun deleteRecordItem(record: Record)
    suspend fun updateRecordItemWithDeletedAccount(account: Account)
    suspend fun updateRecordItemWithDeletedCategory(category: Category)
    fun getRecordById(recordId: String): Flow<TrueRecord>
    fun getAllRecords(): Flow<Resource<List<RecordMap>>>
    fun getAllTrueRecordsWithinSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<Resource<List<TrueRecord>>>

    fun getUserTrueRecordMapsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sortType: SortType,
        currency: List<String>,
    ): Flow<Resource<List<RecordMap>>>

    fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String,
        sortType: SortType,
    ): Flow<Resource<List<RecordMap>>>

    fun getUserAccountRecordsOrderedByDateTime(
        accountId: String,
        sortType: SortType,
    ): Flow<Resource<List<RecordMap>>>

    fun getUserRecordsFromSpecificTime(
        recordType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): Flow<Resource<List<Record>>>

    fun getUserCategoriesWithAmountFromSpecificTime(
        categoryType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): Flow<Resource<List<CategoryWithAmount>>>

    fun getUserAccountsWithAmountFromSpecificTime(
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<Resource<List<AccountWithAmountType>>>

    fun getUserTotalAmountByType(recordType: RecordType): Flow<BalanceItem>
    fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<BalanceItem>

    fun getUserTotalRecordBalance(): Flow<BalanceItem>

}

class RecordRepositoryImpl @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val authRepository: AuthRepository,
    private val recordDataSource: RecordDataSource,
    private val recordDao: RecordDao,
    private val firestore: FirebaseFirestore,
) : RecordRepository {
    private val recordCollection = firestore.collection(
        "record"
    )

    override suspend fun upsertRecordItem(
        record: Record
    ) {
        withContext(Dispatchers.IO) {
            val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
            Log.i(TAG, "upsertRecordItem: $record")

            val tempRecord = if (record.recordId.isEmpty()) {
                val newRecordRef = recordCollection.document()
                record.copy(
                    recordId = newRecordRef.id,
                    userIdFk = currentUserId
                )
            } else {
                record.copy(
                    userIdFk = currentUserId
                )
            }

            recordDao.upsertRecordItem(tempRecord)
            recordDataSource.upsertRecordItem(
                tempRecord
            )
        }
    }

    override suspend fun deleteRecordItem(
        record: Record
    ) {
        withContext(Dispatchers.IO) {
            Log.i(TAG, "deleteRecordItem: $record")
            recordDataSource.deleteRecordItem(record)
            recordDao.deleteRecordItem(record)
        }
    }

    override suspend fun updateRecordItemWithDeletedAccount(account: Account) {
        withContext(Dispatchers.IO) {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val records = recordDao.getUserRecords(userId)
            val tempRecords = records.filter {
                it.accountIdFromFk == account.accountId || it.accountIdToFk == account.accountId
            }.map {
                var record = it
                if (it.accountIdFromFk == account.accountId) {
                    record = record.copy(accountIdFromFk = deletedAccount.accountId)
                }
                if (it.accountIdToFk == account.accountId) {
                    record = record.copy(accountIdToFk = deletedAccount.accountId)
                }
                record
            }
            recordDataSource.upsertAllRecordItem(tempRecords)
            recordDao.upsertAllRecordItem(tempRecords)
        }
    }

    override suspend fun updateRecordItemWithDeletedCategory(category: Category) {
        withContext(Dispatchers.IO) {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val records = recordDao.getUserRecords(userId)
            val tempRecords = records.filter {
                it.categoryIdFk == category.categoryId
            }.map {
                it.copy(categoryIdFk = deletedCategory.categoryId)
            }
            recordDataSource.upsertAllRecordItem(tempRecords)
            recordDao.upsertAllRecordItem(tempRecords)
        }
    }

    override fun getRecordById(recordId: String): Flow<TrueRecord> {
        Log.i(TAG, "getRecordById: $recordId")
        return flow {
            val record = withContext(Dispatchers.IO) {
                recordDao.getRecordById(
                    recordId
                )
            }
            emit(
                record.copy(
                    record = record.record.copy(
                        recordAmount = currencyConverter(
                            record.record.recordAmount,
                            record.toAccount.accountCurrency,
                            record.fromAccount.accountCurrency
                        )
                    )
                )
            )
        }.catch { e ->
            Log.i(
                TAG,
                "getAllTrueRecordsWithinSpecificTime.Error: $e"
            )
        }
    }

    override fun getAllTrueRecordsWithinSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<Resource<List<TrueRecord>>> {
        return flow {
            emit(Resource.Loading())

            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val data = withContext(Dispatchers.IO) {
                recordDao.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
            }
            Log.i(
                TAG,
                "getAllTrueRecordsWithinSpecificTime.Data: $data"
            )
            emit(Resource.Success(data))

        }.catch { e ->
            Log.i(
                TAG,
                "getAllTrueRecordsWithinSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getAllRecords(): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val data = withContext(Dispatchers.IO) {
                recordDao.getUserTrueRecords(userId).groupBy {
                    it.record.recordDateTime.toLocalDate()
                }.map {
                    RecordMap(
                        recordDate = it.key,
                        records = it.value
                    )
                }
            }
            emit(Resource.Success(data))
        }.catch { e ->
            Log.i(
                TAG,
                "getAllRecords.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getUserTrueRecordMapsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sortType: SortType,
        currency: List<String>,
    ): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                TAG,
                "getUserTrueRecordMapsFromSpecificTime: $startDate\n:\n$endDate,\ncurrency: $currency"
            )

            val data = withContext(Dispatchers.IO) {
                recordDao.getUserTrueRecordByCurrencyFromSpecificTime(
                    userId,
                    startDate,
                    endDate,
                    currency
                ).groupBy {
                    it.record.recordDateTime.toLocalDate()
                }.toSortedMap(if (sortType == SortType.DESCENDING) {
                    compareByDescending { it }
                } else {
                    compareBy { it }
                }).map {
                    RecordMap(
                        recordDate = it.key,
                        records = it.value
                    )
                }
            }
            Log.i(
                TAG,
                "getUserTrueRecordMapsFromSpecificTime.data: $data"
            )
            emit(Resource.Success(data))

        }.catch { e ->
            Log.i(
                TAG,
                "getUserTrueRecordMapsFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String,
        sortType: SortType,
    ): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                TAG,
                "getUserCategoryRecordsOrderedByDateTime: $categoryId",

                )
            val data = withContext(Dispatchers.IO) {
                recordDao.getUserCategoryRecordsOrderedByDateTime(
                    userId, categoryId
                ).groupBy {
                    it.record.recordDateTime.toLocalDate()
                }.toSortedMap(if (sortType == SortType.DESCENDING) {
                    compareByDescending { it }
                } else {
                    compareBy { it }
                }).map {
                    RecordMap(
                        recordDate = it.key,
                        records = it.value
                    )
                }
            }
            Log.i(
                TAG,
                "getUserCategoryRecordsOrderedByDateTime.Data: $data"
            )
            emit(Resource.Success(data))
        }.catch { e ->
            Log.i(
                TAG,
                "getUserCategoryRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        accountId: String,
        sortType: SortType,
    ): Flow<Resource<List<RecordMap>>> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            emit(Resource.Loading())
            Log.i(
                TAG,
                "getUserAccountRecordsOrderedByDateTime: $accountId",

                )
            val data = withContext(Dispatchers.IO) {
                recordDao.getUserAccountRecordsOrderedByDateTime(
                    userId, accountId
                ).groupBy {
                    it.record.recordDateTime.toLocalDate()
                }.toSortedMap(if (sortType == SortType.DESCENDING) {
                    compareByDescending { it }
                } else {
                    compareBy { it }
                }).map {
                    RecordMap(
                        recordDate = it.key,
                        records = it.value
                    )
                }
            }
            Log.i(
                TAG,
                "getUserAccountRecordsOrderedByDateTime.data: $data"
            )
            emit(Resource.Success(data))
        }.catch { e ->
            Log.i(
                TAG,
                "getUserAccountRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    override fun getUserRecordsFromSpecificTime(
        recordType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): Flow<Resource<List<Record>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                TAG,
                "getUserRecordsFromSpecificTime: $startDate\n:\n$endDate"
            )
            val records = withContext(Dispatchers.IO) {
                recordDao.getUserRecordsByTypeAndCurrencyFromSpecificTime(
                    userId,
                    recordType,
                    startDate,
                    endDate,
                    currency
                )
            }
            val recordsMap = mutableMapOf<String, Record>()
            records.forEach { record ->
                val key = record.recordDateTime.toLocalDate().toString()
                val existingRecord = recordsMap[key]

                if (existingRecord != null) {
                    val amount = if (record.recordCurrency != existingRecord.recordCurrency) {
                        currencyConverter(
                            record.recordAmount,
                            record.recordCurrency,
                            existingRecord.recordCurrency
                        )
                    } else {
                        record.recordAmount
                    }
                    recordsMap[key] = existingRecord.copy(
                        recordAmount = existingRecord.recordAmount + amount
                    )
                } else {
                    recordsMap[key] = record
                }
            }
            val data = recordsMap.values.toList().let { value ->
                if (sortType == SortType.ASCENDING) {
                    value.sortedBy { it.recordAmount }
                } else {
                    value.sortedByDescending { it.recordAmount }
                }
            }

            Log.i(
                TAG,
                "getUserRecordsFromSpecificTime.Data: $data",

                )
            emit(Resource.Success(data))
        }.catch { e ->
            Log.i(
                TAG,
                "getUserRecordsFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getUserCategoriesWithAmountFromSpecificTime(
        categoryType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): Flow<Resource<List<CategoryWithAmount>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                TAG,
                "getUserCategoriesWithAmountFromSpecificTime: $currency\n$categoryType\n$startDate\n:\n$endDate"
            )
            val userCategories = getUserCategory(
                userId
            )
            val records = withContext(Dispatchers.IO) {
                recordDao.getUserRecordsByTypeAndCurrencyFromSpecificTime(
                    userId,
                    categoryType,
                    startDate,
                    endDate,
                    currency
                )
            }
            val categoryWithAmountMap = mutableMapOf<String, CategoryWithAmount>()
            Log.i(
                TAG,
                "getUserCategoriesWithAmountFromSpecificTimeResult: $records",

                )
            records.forEach { record ->
                val key = record.categoryIdFk + record.recordCurrency

                val existingCategory = categoryWithAmountMap[key]

                if (existingCategory != null) {
                    categoryWithAmountMap[key] = existingCategory.copy(
                        amount = existingCategory.amount + record.recordAmount
                    )
                } else {
                    val newCategory = CategoryWithAmount(
                        category = userCategories.first { it.categoryId == record.categoryIdFk },
                        amount = record.recordAmount,
                        currency = record.recordCurrency
                    )
                    categoryWithAmountMap[key] = newCategory
                }
            }

            val data = withContext(Dispatchers.IO) {
                categoryWithAmountMap.values.toList().let { value ->
                    if (sortType == SortType.ASCENDING) {
                        value.sortedBy { it.amount }
                    } else {
                        value.sortedByDescending { it.amount }
                    }
                }
            }
            Log.i(
                TAG,
                "getUserCategoriesWithAmountFromSpecificTime.Data: $data",

                )
            emit(Resource.Success(data))
        }.catch { e ->
            Log.i(
                TAG,
                "getUserCategoriesWithAmountFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    override fun getUserAccountsWithAmountFromSpecificTime(
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<Resource<List<AccountWithAmountType>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                TAG,
                "getUserAccountsWithAmountFromSpecificTime: \n$startDate\n:\n$endDate"
            )
            val userAccounts = getUserAccount(
                userId
            )
            val records = withContext(Dispatchers.IO) {
                recordDao.getUserRecordsFromSpecificTime(
                    userId, startDate, endDate
                )
            }
            val accountWithAmountMap = mutableMapOf<String, AccountWithAmountType>()
            Log.i(
                TAG,
                "getUserAccountsWithAmountFromSpecificTime.Result: $records",

                )
            userAccounts.forEach { account ->
                val key = account.accountId
                val newAccount = AccountWithAmountType(
                    account = account,
                    incomeAmount = 0.0,
                    expenseAmount = 0.0,
                )
                accountWithAmountMap[key] = newAccount
            }
            records.forEach { record ->
                val key = record.accountIdFromFk
                val existingAccount = accountWithAmountMap[key]
                if (!isTransfer(record.recordType)) {
                    val incomeAmount = if (isIncome(
                            record.recordType
                        )
                    ) record.recordAmount else 0.0
                    val expenseAmount = if (isExpense(
                            record.recordType
                        )
                    ) record.recordAmount else 0.0
                    if (existingAccount != null) {
                        accountWithAmountMap[key] = existingAccount.copy(
                            incomeAmount = existingAccount.incomeAmount + incomeAmount,
                            expenseAmount = existingAccount.expenseAmount + expenseAmount,
                        )
                    } else {
                        val newAccount = AccountWithAmountType(
                            account = userAccounts.first { it.accountId == record.accountIdFromFk },
                            incomeAmount = incomeAmount,
                            expenseAmount = expenseAmount,
                        )
                        accountWithAmountMap[key] = newAccount
                    }
                }
            }

            val data = accountWithAmountMap.values.toList().let { value ->
                if (sortType == SortType.ASCENDING) {
                    value.sortedBy { it.account.accountName }
                } else {
                    value.sortedByDescending { it.account.accountName }
                }
            }
            Log.i(
                TAG,
                "getUserAccountsWithAmountFromSpecificTime.Data: $data",

                )
            emit(Resource.Success(data))

        }.catch { e ->
            Log.i(
                TAG,
                "getUserAccountsWithAmountFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    override fun getUserTotalAmountByType(
        recordType: RecordType
    ): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                TAG,
                "getUserTotalAmountByType: $recordType",

                )
            val records = withContext(Dispatchers.IO) {
                recordDao.getUserRecordsByType(
                    userId, recordType
                )
            }
            val recordTotalAmount = records.sumOf { record ->
                currencyConverter(
                    record.recordAmount,
                    record.recordCurrency,
                    userCurrency
                )
            }
            val data = BalanceItem(
                name = "${recordType.name}: ",
                amount = recordTotalAmount,
                currency = userCurrency
            )
            Log.i(
                TAG,
                "getUserTotalAmountByType.Result: $data",

                )
            emit(data)
        }.catch { e ->
            Log.i(
                TAG,
                "getUserTotalAmountByType.Error: $e"
            )
        }
    }


    override fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                TAG,
                "getUserTotalAmountByTypeFromSpecificTime: $recordType",

                )
            val recordTotalAmount = withContext(Dispatchers.IO) {
                recordDao.getUserRecordsByTypeFromSpecificTime(
                    userId,
                    recordType,
                    startDate,
                    endDate
                ).sumOf { record ->
                    currencyConverter(
                        record.recordAmount,
                        record.recordCurrency,
                        userCurrency
                    )
                }
            }
            val data = BalanceItem(
                name = "${recordType.name}: ",
                amount = recordTotalAmount,
                currency = userCurrency
            )

            Log.i(
                TAG,
                "getUserTotalAmountByTypeFromSpecificTime.Data: $data"
            )
            emit(data)
        }.catch { e ->
            Log.i(
                TAG,
                "getUserTotalAmountByTypeFromSpecificTime.Error: $e"
            )
        }
    }

    override fun getUserTotalRecordBalance(): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                TAG,
                "getUserTotalRecordBalance: ",

                )
            val recordTotalAmount = withContext(Dispatchers.IO) {
                recordDao.getUserRecordsByType(
                    userId, RecordType.Expense
                ).sumOf { record ->
                    currencyConverter(
                        record.recordAmount,
                        record.recordCurrency,
                        userCurrency
                    )
                } + recordDao.getUserRecordsByType(
                    userId, RecordType.Income
                ).sumOf { record ->
                    currencyConverter(
                        record.recordAmount,
                        record.recordCurrency,
                        userCurrency
                    )
                }
            }
            val data = BalanceItem(
                name = "Balance: ",
                amount = recordTotalAmount,
                currency = userCurrency
            )
            Log.i(
                TAG,
                "getUserTotalRecordBalance.Result: $data",

                )
            emit(data)
        }.catch { e ->
            Log.i(
                TAG,
                "getUserTotalRecordBalance.Error: $e"
            )
        }
    }

    private suspend fun getUserAccount(
        userId: String
    ) = withContext(Dispatchers.IO) {
        Firebase.firestore.collection(
            "account"
        ).whereEqualTo(
            "userIdFk", userId
        ).get().await().toObjects<Account>()
    }

    private suspend fun getUserCategory(
        userId: String
    ) = withContext(Dispatchers.IO) {
        Firebase.firestore.collection(
            "category"
        ).whereEqualTo(
            "userIdFk", userId
        ).get().await().toObjects<Category>()
    }

    private suspend fun currencyConverter(
        amount: Double, from: String, to: String
    ): Double {
        return currencyRepository.convertCurrencyData(
            amount, from, to
        ).amount

    }

}


data class CategoryWithAmount(
    val category: Category = Category(),
    val amount: Double = 0.0,
    val currency: String = ""
)

data class AccountWithAmountType(
    val account: Account = Account(),
    val expenseAmount: Double = 0.0,
    val incomeAmount: Double = 0.0
)
