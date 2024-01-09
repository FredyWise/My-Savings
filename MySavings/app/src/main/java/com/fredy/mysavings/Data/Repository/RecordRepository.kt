package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Util.TAG
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime


interface RecordRepository {
    suspend fun upsertRecordItem(record: Record)
    suspend fun deleteRecordItem(record: Record)
    fun getRecordById(recordId: String): Flow<TrueRecord>
    fun getUserRecordsFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>

    fun getUserTrueRecordsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<TrueRecord>>

    fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String,
    ): Flow<List<TrueRecord>>

    fun getUserCategoriesWithAmountFromSpecificTime(
        categoryType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): Flow<List<CategoryWithAmount>>

    fun getUserAccountRecordsOrderedByDateTime(
        accountId: String
    ): Flow<List<TrueRecord>>

    fun getUserTotalAmountByType(recordType: RecordType): Flow<Double>
    fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<Double>

    fun getUserTotalRecordBalance(): Flow<Double>
}

class RecordRepositoryImpl(): RecordRepository {

    override suspend fun upsertRecordItem(
        record: Record
    ) {
        val currentUser = Firebase.auth.currentUser
        Log.i(TAG, "upsertRecordItem: " + record)
        val recordCollection = Firebase.firestore.collection(
            "record"
        )
        if (record.recordId.isEmpty()) {
            recordCollection.add(
                record
            ).addOnSuccessListener { document ->
                recordCollection.document(
                    document.id
                ).set(
                    record.copy(
                        recordId = document.id,
                        userIdFk = currentUser!!.uid
                    )
                )
            }
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
        Log.i(TAG, "deleteRecordItem: " + record)
        Firebase.firestore.collection("record").document(
            record.recordId
        ).delete()
    }

    override fun getRecordById(recordId: String): Flow<TrueRecord> {
        Log.i(TAG, "getRecordById: " + recordId)
        return flow {
            val record = Firebase.firestore.collection(
                "record"
            ).document(
                recordId
            ).get().await().toObject<Record>()!!
            emit(
                getTrueRecord(
                    record
                )
            )

        }
    }

    override fun getUserTrueRecordsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = callbackFlow<List<TrueRecord>> {
        val currentUser = Firebase.auth.currentUser
        Log.i(
            TAG,
            "getUserTrueRecordsFromSpecificTime: $startDate\n:\n$endDate"
        )

        val trueRecordComponentResult = getTrueRecordsComponent()
        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp", TimestampConverter.fromDateTime(endDate)
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.i(
                    TAG,
                    "getUserTrueRecordsFromSpecificTimeError: ${it.message}"
                )
                close(it)
                return@addSnapshotListener
            }

            value?.let {
                val records = it.toObjects<Record>()
                val data = records.map { record ->
                    TrueRecord(
                        record = record,
                        fromAccount = trueRecordComponentResult.fromAccount.single { it.accountId == record.accountIdFromFk },
                        toAccount = trueRecordComponentResult.toAccount.single { it.accountId == record.accountIdToFk },
                        toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
                    )
                }
                Log.i(
                    TAG,
                    "getUserTrueRecordsFromSpecificTimeData: $data"
                )
                trySend(data)
            }
        }

        Log.i(
            TAG,
            "getUserTrueRecordsFromSpecificTime0.0: babi"
        )


        awaitClose {
            listener.remove()
        }
    }


    override fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String,
    ) = callbackFlow<List<TrueRecord>> {
        val currentUser = Firebase.auth.currentUser
        Log.i(
            TAG,
            "getUserCategoryRecordsOrderedByDateTime: " + categoryId,

            )
        val trueRecordComponentResult = getTrueRecordsComponent()
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "categoryIdFk", categoryId
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.i(
                    TAG,
                    "getUserCategoryRecordsOrderedByDateTimeError: ${it.message}"
                )
                close(it)
                return@addSnapshotListener
            }
            value?.let {
                val records = it.toObjects<Record>()
                launch {
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
                    }
                    Log.i(
                        TAG,
                        "getUserCategoryRecordsOrderedByDateTimeData: $data"
                    )
                    trySend(data)
                }
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserRecordsFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = callbackFlow<List<Record>> {
        val currentUser = Firebase.auth.currentUser
        Log.i(
            TAG,
            "getUserRecordsFromSpecificTime: $startDate\n:\n$endDate"
        )

        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp", TimestampConverter.fromDateTime(endDate)
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
                    TAG,
                    "getUserRecordsFromSpecificTimeError: ${it.message}"
                )
                close(it)
                return@addSnapshotListener
            }

            value?.let {
                val records = it.toObjects<Record>()
                val recordsMap: MutableMap<String, Record> = mutableMapOf()
                Log.i(
                    TAG,
                    "getUserRecordsFromSpecificTime: " + records
                )
                records.forEach { record ->
                    if (record != null) {
                        val date = record.recordDateTime.toLocalDate().toString()
                        val currency = record.recordCurrency
                        val existingRecord = recordsMap[date]

                        if (existingRecord != null) {
                            recordsMap[date] = existingRecord.copy(
                                recordAmount = existingRecord.recordAmount + record.recordAmount
                            )
                        } else {
                            recordsMap[date] = record.copy(
                                recordType = recordType
                            )
                        }
                    }

                }

                val data = recordsMap.values.toList()
                Log.i(
                    TAG,
                    "getUserRecordsFromSpecificTimeData: " + data,

                    )
                trySend(data)
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
    ) = callbackFlow<List<CategoryWithAmount>> {
        val currentUser = Firebase.auth.currentUser
        Log.i(
            TAG,
            "getUserCategoriesWithAmountFromSpecificTime: $currency\n$categoryType\n$startDate\n:\n$endDate"
        )

        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp", TimestampConverter.fromDateTime(endDate)
        ).whereEqualTo(
            "recordType", categoryType
        ).whereIn("recordCurrency",
            currency.ifEmpty { listOf("") }).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.i(
                    TAG,
                    "getUserCategoriesWithAmountFromSpecificTimeError: ${it.message}"
                )
                close(it)
            }

            value?.let {
                val records = it.toObjects<Record>()
                val categoriesWithAmount = mutableMapOf<String, CategoryWithAmount>()
                Log.i(
                    TAG,
                    "getUserCategoriesWithAmountFromSpecificTimeResult: " + records,

                    )
                records.forEach { record ->
                    val key = record.categoryIdFk + record.recordCurrency
                    val currency = record.recordCurrency

                    val existingCategory = categoriesWithAmount[key]

                    if (existingCategory != null && existingCategory.currency == currency) {
                        categoriesWithAmount[key] = existingCategory.copy(
                            amount = existingCategory.amount + record.recordAmount
                        )
                    } else {
                        val newCategory = CategoryWithAmount(
                            categoryId = record.categoryIdFk,
                            amount = record.recordAmount,
                            currency = currency
                        )
                        categoriesWithAmount[key] = newCategory
                    }
                }

                launch {
                    val deferredCategoryDetails = categoriesWithAmount.map { (_, categoryWithAmount) ->
                        async {
                            val category = Firebase.firestore.collection(
                                "category"
                            ).document(
                                categoryWithAmount.categoryId
                            ).get().await().toObject<Category>()
                            categoryWithAmount.copy(
                                category = category ?: Category()
                            )
                        }
                    }

                    val data = deferredCategoryDetails.awaitAll().sortedBy { it.amount }
                    Log.i(
                        TAG,
                        "getUserCategoriesWithAmountFromSpecificTimeData: " + data,

                        )
                    trySend(data)
                }
            }
        }
        Log.i(
            TAG,
            "getUserCategoriesWithAmountFromSpecificTime0.0: " + listener
        )
        awaitClose {
            listener.remove()
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        accountId: String,
    ) = callbackFlow<List<TrueRecord>> {
        val currentUser = Firebase.auth.currentUser
        Log.i(
            TAG,
            "getUserAccountRecordsOrderedByDateTime: " + accountId,

            )
        val trueRecordComponentResult = getTrueRecordsComponent()
        val listener = Firebase.firestore.collection(
            "record"
        ).where(//wasent tested
            Filter.or(
                Filter.equalTo(
                    "accountIdFromFk",
                    accountId
                ),
                Filter.equalTo(
                    "accountIdToFk",
                    accountId
                )
            )
        ).whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let {
                val records = it.toObjects<Record>()
                launch {
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
                    }
                    Log.i(
                        TAG,
                        "getUserAccountRecordsOrderedByDateTime.0: $data"
                    )
                    trySend(data)

                }
            }
        }
        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalAmountByType(
        recordType: RecordType
    ) = callbackFlow<Double> {
        val currentUser = Firebase.auth.currentUser
        Log.i(
            TAG,
            "getUserTotalAmountByType: " + recordType,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
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
                    "getUserTotalAmountByTypeResult: " + data,

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
        val currentUser = Firebase.auth.currentUser
        Log.i(
            TAG,
            "getUserTotalAmountByTypeFromSpecificTime: " + recordType,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordTimestamp",
            TimestampConverter.fromDateTime(
                startDate
            )
        ).whereLessThanOrEqualTo(
            "recordTimestamp", TimestampConverter.fromDateTime(endDate)
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
                    "getUserTotalAmountByTypeFromSpecificTime: " + data
                )
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalRecordBalance() = callbackFlow<Double> {
        val currentUser = Firebase.auth.currentUser
        Log.i(
            TAG,
            "getUserTotalRecordBalance: ",

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
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
                    "getUserTotalRecordBalanceResult: " + data,

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
        val currentUser = Firebase.auth.currentUser

        val fromAccountDeferred = async {
            Firebase.firestore.collection("account").whereEqualTo(
                "userIdFk",
                if (currentUser.isNotNull()) currentUser!!.uid else ""
            ).get().await().toObjects<Account>()
        }

        val toAccountDeferred = async {
            Firebase.firestore.collection("account").whereEqualTo(
                "userIdFk",
                if (currentUser.isNotNull()) currentUser!!.uid else ""
            ).get().await().toObjects<Account>()
        }

        val toCategoryDeferred = async {
            Firebase.firestore.collection("category").whereEqualTo(
                "userIdFk",
                if (currentUser.isNotNull()) currentUser!!.uid else ""
            ).get().await().toObjects<Category>()
        }

        TrueRecordComponentResult(
            fromAccount = fromAccountDeferred.await(),
            toAccount = toAccountDeferred.await(),
            toCategory = toCategoryDeferred.await()
        )
    }

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
    val categoryId: String = "",
    val amount: Double = 0.0,
    val currency: String = ""
)
