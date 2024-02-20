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
import com.fredy.mysavings.Data.Mappers.filterRecordCurrency
import com.fredy.mysavings.Data.Mappers.filterTrueRecordCurrency
import com.fredy.mysavings.Data.Mappers.toRecordSortedMaps
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String,
        sortType: SortType,
    ): Flow<Resource<List<RecordMap>>>

    fun getUserAccountRecordsOrderedByDateTime(
        accountId: String,
        sortType: SortType,
    ): Flow<Resource<List<RecordMap>>>

    fun getUserTrueRecordMapsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sortType: SortType,
        currency: List<String>,
        useUserCurrency: Boolean = false,
    ): Flow<Resource<List<RecordMap>>>


    fun getUserRecordsFromSpecificTime(
        recordType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
        useUserCurrency: Boolean = true,
    ): Flow<Resource<List<Record>>>

    fun getUserCategoriesWithAmountFromSpecificTime(
        categoryType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
        useUserCurrency: Boolean = true,
    ): Flow<Resource<List<CategoryWithAmount>>>

    fun getUserAccountsWithAmountFromSpecificTime(
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        useUserCurrency: Boolean = false,
    ): Flow<Resource<List<AccountWithAmountType>>>

    fun getUserTotalAmountByType(recordType: RecordType): Flow<BalanceItem>
    fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<BalanceItem>

    fun getUserTotalRecordBalance(
        startDate: LocalDateTime = LocalDateTime.of(2000,1,1,1,1),
        endDate: LocalDateTime
    ): Flow<BalanceItem>

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
            val records = recordDataSource.getUserRecords(userId).first()
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
            val records = recordDataSource.getUserRecords(userId).first()
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
                recordDataSource.getRecordById(
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
            withContext(Dispatchers.IO) {
                recordDataSource.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
            }.collect { data ->
                Log.i(
                    TAG,
                    "getAllTrueRecordsWithinSpecificTime.Data: $data"
                )
                emit(Resource.Success(data))
            }

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
            withContext(Dispatchers.IO) {
                recordDataSource.getUserTrueRecords(userId).map { records ->
                    records.toRecordSortedMaps()
                }
            }.collect { data ->
                Log.i(
                    TAG,
                    "getAllRecords.data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.i(
                TAG,
                "getAllRecords.Error: $e"
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
            withContext(Dispatchers.IO) {
                recordDataSource.getUserCategoryRecordsOrderedByDateTime(
                    userId, categoryId
                ).map { records ->
                    records.toRecordSortedMaps()
                }
            }.collect { data ->
                Log.i(
                    TAG,
                    "getUserCategoryRecordsOrderedByDateTime.Data: $data"
                )
                emit(Resource.Success(data))
            }
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
            withContext(Dispatchers.IO) {
                recordDataSource.getUserAccountRecordsOrderedByDateTime(
                    userId, accountId
                ).map { records ->
                    records.toRecordSortedMaps()
                }
            }.collect { data ->
                Log.i(
                    TAG,
                    "getUserAccountRecordsOrderedByDateTime.data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.i(
                TAG,
                "getUserAccountRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getUserTrueRecordMapsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sortType: SortType,
        currency: List<String>,
        useUserCurrency: Boolean,
    ): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                TAG,
                "getUserTrueRecordMapsFromSpecificTime: $startDate\n:\n$endDate,\ncurrency: $currency"
            )
            withContext(Dispatchers.IO) {
                recordDataSource.getUserTrueRecordsFromSpecificTime(
                    userId,
                    startDate,
                    endDate,
                ).map { records ->
                    records.convertRecordCurrency(userCurrency, useUserCurrency)
                        .filterTrueRecordCurrency(currency).toRecordSortedMaps(sortType)
                }
            }.collect { data ->
                emit(Resource.Success(data))
            }

        }.catch { e ->
            Log.i(
                TAG,
                "getUserTrueRecordMapsFromSpecificTime.Error: $e"
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
        useUserCurrency: Boolean,
    ): Flow<Resource<List<Record>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                TAG,
                "getUserRecordsFromSpecificTime: $startDate\n:\n$endDate"
            )
            withContext(Dispatchers.IO) {
                recordDataSource.getUserRecordsByTypeFromSpecificTime(
                    userId,
                    recordType,
                    startDate,
                    endDate,
                ).map { it.filterRecordCurrency(currency) }
            }.collect { records ->
                val data = records.combineSameCurrencyData(sortType, userCurrency, useUserCurrency)

                Log.i(
                    TAG,
                    "getUserRecordsFromSpecificTime.Data: $data",

                    )
                emit(Resource.Success(data))
            }
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
        useUserCurrency: Boolean,
    ): Flow<Resource<List<CategoryWithAmount>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                TAG,
                "getUserCategoriesWithAmountFromSpecificTime: $currency\n$categoryType\n$startDate\n:\n$endDate"
            )
            val userCategories = getUserCategory(
                userId
            )
            withContext(Dispatchers.IO) {
                recordDataSource.getUserRecordsByTypeFromSpecificTime(
                    userId,
                    categoryType,
                    startDate,
                    endDate,
                ).map { it.filterRecordCurrency(currency) }
            }.collect { records ->
                Log.i(
                    TAG,
                    "getUserCategoriesWithAmountFromSpecificTime.Result: $records",
                )
                val data =
                    records.combineSameCurrencyCategory(
                        sortType,
                        userCategories,
                        userCurrency,
                        useUserCurrency
                    )
                Log.i(
                    TAG,
                    "getUserCategoriesWithAmountFromSpecificTime.Data: $data",

                    )
                emit(Resource.Success(data))
            }
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
        useUserCurrency: Boolean,
    ): Flow<Resource<List<AccountWithAmountType>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                TAG,
                "getUserAccountsWithAmountFromSpecificTime: \n$startDate\n:\n$endDate"
            )
            val userAccounts = getUserAccount(
                userId
            )
            withContext(Dispatchers.IO) {
                recordDataSource.getUserRecordsFromSpecificTime(
                    userId, startDate, endDate
                )
            }.collect { records ->
                Log.i(
                    TAG,
                    "getUserAccountsWithAmountFromSpecificTime.Result: $records",
                )
                val data = records.combineSameCurrencyAccount(
                    sortType,
                    userAccounts,
                    userCurrency,
                    useUserCurrency
                )
                Log.i(
                    TAG,
                    "getUserAccountsWithAmountFromSpecificTime.Data: $data",

                    )
                emit(Resource.Success(data))
            }
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
                recordDataSource.getUserRecordsByType(
                    userId, recordType
                ).map { it.getTotalRecordBalance(userCurrency) }
            }

            records.collect { recordTotalAmount ->
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
            }
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
            withContext(Dispatchers.IO) {
                recordDataSource.getUserRecordsByTypeFromSpecificTime(
                    userId,
                    recordType,
                    startDate,
                    endDate
                ).map { it.getTotalRecordBalance(userCurrency) }
            }.collect { recordTotalAmount ->
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
            }
        }.catch { e ->
            Log.i(
                TAG,
                "getUserTotalAmountByTypeFromSpecificTime.Error: $e"
            )
        }
    }

    override fun getUserTotalRecordBalance(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                TAG,
                "getUserTotalRecordBalance: ",

                )

            withContext(Dispatchers.IO) {
                recordDataSource.getUserRecordsByTypeFromSpecificTime(
                    userId,
                    null,
                    startDate,
                    endDate
                ).map { it.getTotalRecordBalance(userCurrency) }
            }.collect { recordTotalAmount ->
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
            }


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

    private suspend fun List<Record>.getTotalRecordBalance(userCurrency: String): Double {
        return this.sumOf { record ->
            currencyConverter(
                record.recordAmount,
                record.recordCurrency,
                userCurrency
            )
        }
    }

    private suspend fun List<TrueRecord>.convertRecordCurrency(
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<TrueRecord> {
        return if (useUserCurrency) {
            this.map { trueRecord ->
                trueRecord.copy(
                    record = trueRecord.record.copy(
                        recordAmount = currencyConverter(
                            trueRecord.record.recordAmount,
                            trueRecord.record.recordCurrency,
                            userCurrency
                        ),
                        recordCurrency = userCurrency
                    )
                )
            }
        } else {
            this
        }
    }

    private suspend fun List<Record>.combineSameCurrencyData(
        sortType: SortType = SortType.DESCENDING,
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<Record> {
        val recordsMap = mutableMapOf<String, Record>()
        this.forEach { record ->
            val key = record.recordDateTime.toLocalDate().toString()
            val existingRecord = recordsMap[key]
            val currency = if (useUserCurrency) userCurrency else record.recordCurrency
            val amount = if (useUserCurrency) {
                currencyConverter(
                    record.recordAmount,
                    record.recordCurrency,
                    userCurrency
                )
            } else {
                record.recordAmount
            }

            if (existingRecord != null) {
                val tempAmount = if (record.recordCurrency != existingRecord.recordCurrency) {
                    currencyConverter(
                        amount,
                        record.recordCurrency,
                        existingRecord.recordCurrency
                    )
                } else {
                    amount
                }

                recordsMap[key] = existingRecord.copy(
                    recordAmount = existingRecord.recordAmount + tempAmount,
                )
            } else {
                recordsMap[key] = record.copy(recordAmount = amount,recordCurrency = currency, )
            }
        }
        val data = recordsMap.values.toList().let { value ->
            if (sortType == SortType.ASCENDING) {
                value.sortedBy { it.recordAmount }
            } else {
                value.sortedByDescending { it.recordAmount }
            }
        }
        return data
    }

    private suspend fun List<Record>.combineSameCurrencyCategory(
        sortType: SortType = SortType.DESCENDING,
        userCategories: List<Category>,
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<CategoryWithAmount> {
        val categoryWithAmountMap = mutableMapOf<String, CategoryWithAmount>()
        this.forEach { record ->
            val currency = if (useUserCurrency) userCurrency else record.recordCurrency
            val key = record.categoryIdFk + currency
            val existingCategory = categoryWithAmountMap[key]
            val amount = if (useUserCurrency) {
                currencyConverter(
                    record.recordAmount,
                    record.recordCurrency,
                    userCurrency
                )
            } else {
                record.recordAmount
            }
            if (existingCategory != null) {
                categoryWithAmountMap[key] = existingCategory.copy(
                    amount = existingCategory.amount + amount,
                )
            } else {
                val newCategory = CategoryWithAmount(
                    category = userCategories.first { it.categoryId == record.categoryIdFk },
                    amount = amount,
                    currency = currency
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
        return data
    }

    private suspend fun List<Record>.combineSameCurrencyAccount(
        sortType: SortType = SortType.DESCENDING,
        userAccounts: List<Account>,
        userCurrency: String,
        useUserCurrency: Boolean,
    ): List<AccountWithAmountType> {
        val accountWithAmountMap = mutableMapOf<String, AccountWithAmountType>()
        userAccounts.forEach { account ->
            val key = account.accountId
            val newAccount = AccountWithAmountType(
                account = account,
                incomeAmount = 0.0,
                expenseAmount = 0.0,
            )
            accountWithAmountMap[key] = newAccount
        }
        this.forEach { record ->
            val key = record.accountIdFromFk
            val existingAccount = accountWithAmountMap[key]
            if (!isTransfer(record.recordType)) {
                val amount = if (useUserCurrency) {
                    currencyConverter(
                        record.recordAmount,
                        record.recordCurrency,
                        userCurrency
                    )
                } else {
                    record.recordAmount
                }
                val incomeAmount = if (isIncome(
                        record.recordType
                    )
                ) amount else 0.0
                val expenseAmount = if (isExpense(
                        record.recordType
                    )
                ) amount else 0.0
                if (existingAccount != null) {
                    val currency =
                        if (useUserCurrency) userCurrency else existingAccount.account.accountCurrency
                    accountWithAmountMap[key] = existingAccount.copy(
                        account = existingAccount.account.copy(accountCurrency = currency),
                        incomeAmount = existingAccount.incomeAmount + incomeAmount,
                        expenseAmount = existingAccount.expenseAmount + expenseAmount,
                    )
                } else {
                    val account = userAccounts.first { it.accountId == record.accountIdFromFk }
                    val currency = if (useUserCurrency) userCurrency else account.accountCurrency
                    val newAccount = AccountWithAmountType(
                        account = account.copy(accountCurrency = currency),
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
        return data
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
