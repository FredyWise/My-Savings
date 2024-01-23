package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.RecordMap
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject


interface RecordRepository {
    suspend fun upsertRecordItem(record: Record)
    suspend fun deleteRecordItem(record: Record)
    fun getRecordById(recordId: String): Flow<TrueRecord>
    fun getUserRecordsFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): Flow<Resource<List<Record>>>

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

    fun getUserCategoriesWithAmountFromSpecificTime(
        categoryType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): Flow<Resource<List<CategoryWithAmount>>>

    fun getUserAccountsWithAmountFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<Resource<List<AccountWithAmountType>>>

    fun getUserTotalAmountByType(recordType: RecordType): Flow<Double>
    fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<Double>

    fun getUserTotalRecordBalance(): Flow<Double>
}

class RecordRepositoryImpl @Inject constructor(
    currencyRepository: CurrencyRepository,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): RecordRepository {
    private val recordCollection = firestore.collection(
        "record"
    )

    override suspend fun upsertRecordItem(
        record: Record
    ) {
        val currentUser = firebaseAuth.currentUser
        Log.i(TAG, "upsertRecordItem: $record")

        if (record.recordId.isEmpty()) {
            val newRecordRef = recordCollection.document()
            newRecordRef.set(
                record.copy(
                    recordId = newRecordRef.id,
                    userIdFk = currentUser!!.uid
                )
            )
        } else {
            recordCollection.document(
                record.recordId
            ).set(
                record.copy(
                    userIdFk = currentUser!!.uid
                )
            )
        }
    }

    override suspend fun deleteRecordItem(
        record: Record
    ) {
        Log.i(TAG, "deleteRecordItem: $record")
        recordCollection.document(
            record.recordId
        ).delete()
    }

    override fun getRecordById(recordId: String): Flow<TrueRecord> {
        Log.i(TAG, "getRecordById: $recordId")
        return flow {
            val record = recordCollection.document(
                recordId
            ).get().await().toObject<Record>()!!
            emit(
                getTrueRecord(
                    record
                )
            )

        }
    }

    override fun getUserTrueRecordMapsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sortType: SortType,
        currency: List<String>,
    ) = callbackFlow<Resource<List<RecordMap>>> {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserTrueRecordMapsFromSpecificTime: $startDate\n:\n$endDate,\ncurrency: $currency"
        )

        val trueRecordComponentResult = getTrueRecordsComponent()
        val listener = recordCollection.whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                endDate
            )
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let { e ->
                Log.e(
                    TAG,
                    "getUserTrueRecordMapsFromSpecificTimeError: ${e.message}"
                )
                e.message?.let {
                    trySend(Resource.Error(it))
                }
            }

            value?.let { query ->
                val records = query.toObjects<Record>().filter { currency.contains(it.recordCurrency) || currency.isEmpty() }
                val data = records.map { record ->
                    TrueRecord(
                        record = record,
                        fromAccount = trueRecordComponentResult.fromAccount.single { it.accountId == record.accountIdFromFk },
                        toAccount = trueRecordComponentResult.toAccount.single { it.accountId == record.accountIdToFk },
                        toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
                    )
                }.groupBy {
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
                Log.i(
                    TAG,
                    "getUserTrueRecordMapsFromSpecificTimeData: $data"
                )
                trySend(Resource.Success(data))
            }
        }

        Log.i(
            TAG,
            "getUserTrueRecordMapsFromSpecificTime0.0: babi"
        )


        awaitClose {
            listener.remove()
        }
    }


    override fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String,
        sortType: SortType,
    ) = callbackFlow<Resource<List<RecordMap>>> {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserCategoryRecordsOrderedByDateTime: $categoryId",

            )
        val trueRecordComponentResult = getTrueRecordsComponent()
        val listener = recordCollection.whereEqualTo(
            "categoryIdFk", categoryId
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let { e ->
                Log.e(
                    TAG,
                    "getUserCategoryRecordsOrderedByDateTimeError: ${e.message}"
                )
                e.message?.let {
                    trySend(Resource.Error(it))
                }
            }
            value?.let { query ->
                val records = query.toObjects<Record>()
                val data = records.map { record ->
                    Log.i(
                        TAG,
                        "getUserCategoryRecordsOrderedByDateTimeDocument: $record"
                    )
                    TrueRecord(
                        record = record,
                        fromAccount = trueRecordComponentResult.fromAccount.single { it.accountId == record.accountIdFromFk },
                        toAccount = trueRecordComponentResult.toAccount.single { it.accountId == record.accountIdToFk },
                        toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
                    )
                }.groupBy {
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
                Log.i(
                    TAG,
                    "getUserCategoryRecordsOrderedByDateTimeData: $data"
                )
                trySend(Resource.Success(data))
            }

        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        accountId: String,
        sortType: SortType,
    ) = callbackFlow<Resource<List<RecordMap>>> {
        val currentUser = firebaseAuth.currentUser
        trySend(Resource.Loading())
        Log.i(
            TAG,
            "getUserAccountRecordsOrderedByDateTime: $accountId",

            )
        val trueRecordComponentResult = getTrueRecordsComponent()
        val listener = recordCollection.where(//wasent tested
            Filter.or(
                Filter.equalTo(
                    "accountIdFromFk", accountId
                ), Filter.equalTo(
                    "accountIdToFk", accountId
                )
            )
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let { e ->
                Log.e(
                    TAG,
                    "getUserAccountRecordsOrderedByDateTimeError: " + e.message,

                    )
                e.message?.let {
                    trySend(Resource.Error(it))
                }
            }
            value?.let { query ->
                val records = query.toObjects<Record>()
                val data = records.map { record ->
                    Log.i(
                        TAG,
                        "getUserAccountRecordsOrderedByDateTime.1: $record"
                    )
                    TrueRecord(
                        record = record,
                        fromAccount = trueRecordComponentResult.fromAccount.single { it.accountId == record.accountIdFromFk },
                        toAccount = trueRecordComponentResult.toAccount.single { it.accountId == record.accountIdToFk },
                        toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
                    )
                }.groupBy {
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
                Log.i(
                    TAG,
                    "getUserAccountRecordsOrderedByDateTime.0: $data"
                )
                trySend(Resource.Success(data))

            }
        }
        awaitClose {
            listener.remove()
        }
    }

    override fun getUserRecordsFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ) = callbackFlow<Resource<List<Record>>> {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserRecordsFromSpecificTime: $startDate\n:\n$endDate"
        )

        val listener = recordCollection.whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                endDate
            )
        ).whereEqualTo(
            "recordType", recordType
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let { e ->
                Log.e(
                    TAG,
                    "getUserRecordsFromSpecificTimeError: ${e.message}"
                )
                e.message?.let {
                    trySend(Resource.Error(it))
                }
            }

            value?.let { query ->
                val records = query.toObjects<Record>().filter { currency.contains(it.recordCurrency) || currency.isEmpty() }
                val recordsMap = mutableMapOf<String, Record>()
                Log.i(
                    TAG,
                    "getUserRecordsFromSpecificTime: $records"
                )
                records.forEach { record ->
                    val key = record.recordDateTime.toLocalDate().toString() + record.recordCurrency
                    val existingRecord = recordsMap[key]

                    if (existingRecord != null) {
                        recordsMap[key] = existingRecord.copy(
                            recordAmount = existingRecord.recordAmount + record.recordAmount
                        )
                    } else {
                        recordsMap[key] = record
                    }
                }

                val data = recordsMap.values.toList()
                Log.i(
                    TAG,
                    "getUserRecordsFromSpecificTimeData: $data",

                    )
                trySend(Resource.Success(data))
            }
        }

        Log.i(
            TAG,
            "getUserRecordsFromSpecificTime0.0: babi"
        )

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserCategoriesWithAmountFromSpecificTime(
        categoryType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ) = callbackFlow<Resource<List<CategoryWithAmount>>> {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserCategoriesWithAmountFromSpecificTime: $currency\n$categoryType\n$startDate\n:\n$endDate"
        )
        val userCategories = getUserCategory(
            currentUser
        )
        val listener = recordCollection.whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                endDate
            )
        ).whereEqualTo(
            "recordType", categoryType
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let { e ->
                Log.i(
                    TAG,
                    "getUserCategoriesWithAmountFromSpecificTimeError: ${e.message}"
                )
                e.message?.let {
                    trySend(Resource.Error(it))
                }
            }

            value?.let { query ->
                val records = query.toObjects<Record>().filter { currency.contains(it.recordCurrency) || currency.isEmpty() }
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

                val data = categoryWithAmountMap.values.toList().sortedBy { it.amount }
                Log.i(
                    TAG,
                    "getUserCategoriesWithAmountFromSpecificTimeData: $data",

                    )
                trySend(Resource.Success(data))

            }
        }
        Log.i(
            TAG,
            "getUserCategoriesWithAmountFromSpecificTime0.0: $listener"
        )
        awaitClose {
            listener.remove()
        }
    }

    override fun getUserAccountsWithAmountFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ) = callbackFlow<Resource<List<AccountWithAmountType>>> {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserAccountsWithAmountFromSpecificTime: \n$startDate\n:\n$endDate"
        )
        val userAccounts = getUserAccount(
            currentUser
        )
        val listener = recordCollection.whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                endDate
            )
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let { e ->
                Log.i(
                    TAG,
                    "getUserAccountsWithAmountFromSpecificTimeError: ${e.message}"
                )
                e.message?.let {
                    trySend(Resource.Error(it))
                }
            }

            value?.let { query ->
                val records = query.toObjects<Record>()
                val accountWithAmountMap = mutableMapOf<String, AccountWithAmountType>()
                Log.i(
                    TAG,
                    "getUserAccountsWithAmountFromSpecificTimeResult: $records",

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
                        val incomeAmount = if (isIncome(record.recordType)) record.recordAmount else 0.0
                        val expenseAmount = if (isExpense(record.recordType)) record.recordAmount else 0.0
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

                val data = accountWithAmountMap.values.toList().sortedBy { it.account.accountName }
                Log.i(
                    TAG,
                    "getUserAccountsWithAmountFromSpecificTimeData: $data",

                    )
                trySend(Resource.Success(data))

            }
        }
        Log.i(
            TAG,
            "getUserAccountsWithAmountFromSpecificTime0.0: $listener"
        )
        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalAmountByType(
        recordType: RecordType
    ) = callbackFlow<Double> {
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserTotalAmountByType: $recordType",

            )
        val listener = recordCollection.whereEqualTo(
            "recordType", recordType
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.i(
                    TAG,
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.sumOf { document ->
                    document.toObject<Record>().recordAmount
                }
                Log.i(
                    TAG,
                    "getUserTotalAmountByTypeResult: $data",

                    )
                trySend(data)

            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = callbackFlow<Double> {
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserTotalAmountByTypeFromSpecificTime: $recordType",

            )
        val listener = recordCollection.whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                endDate
            )
        ).whereEqualTo(
            "recordType", recordType
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.i(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.toObjects<Record>().sumOf { record ->
                    record.recordAmount
                }
                Log.i(
                    TAG,
                    "getUserTotalAmountByTypeFromSpecificTime: $data"
                )
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalRecordBalance() = callbackFlow<Double> {
        val currentUser = firebaseAuth.currentUser
        Log.i(
            TAG,
            "getUserTotalRecordBalance: ",

            )
        val listener = recordCollection.whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).whereNotEqualTo(
            "recordType", RecordType.Transfer
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.i(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.toObjects<Record>().sumOf { record ->
                    record.recordAmount
                }
                Log.i(
                    TAG,
                    "getUserTotalRecordBalanceResult: $data",

                    )
                trySend(data)

            }
        }

        awaitClose {
            listener.remove()
        }
    }

    private suspend fun getTrueRecord(record: Record) = coroutineScope {
        val fromAccountDeferred = async {
            Firebase.firestore.collection("account").document(
                record.accountIdFromFk
            ).get().await()
        }

        val toAccountDeferred = async {
            Firebase.firestore.collection("account").document(
                record.accountIdToFk
            ).get().await()
        }

        val toCategoryDeferred = async {
            Firebase.firestore.collection("category").document(
                record.categoryIdFk
            ).get().await()
        }

        TrueRecord(
            record = record,
            fromAccount = fromAccountDeferred.await().toObject<Account>()!!,
            toAccount = toAccountDeferred.await().toObject<Account>()!!,
            toCategory = toCategoryDeferred.await().toObject<Category>()!!
        )
    }

    private suspend fun getTrueRecordsComponent() = coroutineScope {
        val currentUser = firebaseAuth.currentUser

        val fromAccountDeferred = async {
            getUserAccount(currentUser)
        }

        val toAccountDeferred = async {
            getUserAccount(currentUser)
        }

        val toCategoryDeferred = async {
            getUserCategory(currentUser)
        }

        TrueRecordComponentResult(
            fromAccount = fromAccountDeferred.await(),
            toAccount = toAccountDeferred.await(),
            toCategory = toCategoryDeferred.await()
        )
    }

    private suspend fun getUserAccount(
        currentUser: FirebaseUser?
    ) = Firebase.firestore.collection(
        "account"
    ).whereEqualTo(
        "userIdFk",
        if (currentUser.isNotNull()) currentUser!!.uid else ""
    ).get().await().toObjects<Account>()

    private suspend fun getUserCategory(
        currentUser: FirebaseUser?
    ) = Firebase.firestore.collection(
        "category"
    ).whereEqualTo(
        "userIdFk",
        if (currentUser.isNotNull()) currentUser!!.uid else ""
    ).get().await().toObjects<Category>()
}


data class TrueRecordComponentResult(
    val fromAccount: List<Account>,
    val toAccount: List<Account>,
    val toCategory: List<Category>,
)

data class TrueRecord(
    val record: Record = Record(),
    val fromAccount: Account = Account(),
    val toAccount: Account = Account(),
    val toCategory: Category = Category(),
)

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
