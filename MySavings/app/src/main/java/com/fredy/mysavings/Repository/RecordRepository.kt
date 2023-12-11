package com.fredy.mysavings.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.currencyCodes
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime


interface RecordRepository {
    suspend fun upsertRecordItem(record: Record)
    suspend fun deleteRecordItem(record: Record)
    fun getRecordById(recordId: String): Flow<TrueRecord>
    fun getUserRecordsFromSpecificTime(
        recordType: RecordType,
        startDate: Timestamp,
        endDate: Timestamp
    ): Flow<List<Record>>
    fun getUserTrueRecordsFromSpecificTime(
        startDate: Timestamp,
        endDate: Timestamp
    ): Flow<List<TrueRecord>>

    fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String,
    ): Flow<List<TrueRecord>>

    fun getUserCategoriesWithAmountFromSpecificTime(
        categoryType: RecordType,
        startDate: Timestamp,
        endDate: Timestamp,
        currency: List<String>,
    ): Flow<List<CategoryWithAmount>>

    fun getUserAccountRecordsOrderedByDateTime(
        accountId: String
    ): Flow<List<TrueRecord>>

    fun getUserTotalAmountByType(recordType: RecordType): Flow<Double>
    fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: Timestamp,
        endDate: Timestamp
    ): Flow<Double>

    fun getUserTotalRecordBalance(): Flow<Double>
}

class RecordRepositoryImpl(): RecordRepository {

    override suspend fun upsertRecordItem(
        record: Record
    ) {
        val currentUser = Firebase.auth.currentUser
        Log.e(TAG, "upsertRecordItem: " + record)
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
        Log.e(TAG, "deleteRecordItem: " + record)
        Firebase.firestore.collection("record").document(
            record.recordId
        ).delete()
    }

    override fun getRecordById(recordId: String): Flow<TrueRecord> {
        Log.e(TAG, "getRecordById: " + recordId)
        return flow {
            val record = Firebase.firestore.collection(
                "record"
            ).document(
                recordId
            ).get().await().toObject<Record>()!!
            val result = getDocuments(
                record
            )
            emit(
                TrueRecord(
                    record = record,
                    fromAccount = result.fromAccount.toObject<Account>()!!,
                    toAccount = result.toAccount.toObject<Account>()!!,
                    toCategory = result.toCategory.toObject<Category>()!!
                )
            )

        }
    }

    override fun getUserTrueRecordsFromSpecificTime(
        startDate: Timestamp,
        endDate: Timestamp
    ) = callbackFlow<List<TrueRecord>> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserTrueRecordsFromSpecificTime: $startDate\n:\n$endDate"
        )

        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordTimestamp", startDate
        ).whereLessThanOrEqualTo(
            "recordTimestamp", endDate
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    TAG,
                    "getUserTrueRecordsFromSpecificTimeError: ${it.message}"
                )
                close(it)
                return@addSnapshotListener
            }

            value?.let { it ->
                val recordDocuments = it.documents
                launch {
                    val data = recordDocuments.map { document ->
                        Log.e(
                            TAG,
                            "getUserTrueRecordsFromSpecificTimeDocument: $document"
                        )
                        val record = document.toObject<Record>()!!
                        val result = getDocuments(
                            record
                        )
                        val fromAccount = result.fromAccount.toObject<Account>()!!
                        val toAccount = result.toAccount.toObject<Account>()!!
                        val toCategory = result.toCategory.toObject<Category>()!!

                        TrueRecord(
                            record = record,
                            fromAccount = fromAccount,
                            toAccount = toAccount,
                            toCategory = toCategory
                        )
                    }
                    Log.e(
                        TAG,
                        "getUserTrueRecordsFromSpecificTimeData: $data"
                    )
                    trySend(data)

                }
            }
        }

        Log.e(
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
        Log.e(
            TAG,
            "getUserCategoryRecordsOrderedByDateTime: " + categoryId,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "categoryIdFk", categoryId
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    TAG,
                    "getUserCategoryRecordsOrderedByDateTimeError: ${it.message}"
                )
                close(it)
                return@addSnapshotListener
            }
            value?.let { result ->
                val recordDocuments = result.documents
                launch {
                    val data = recordDocuments.map { document ->
                        Log.e(
                            TAG,
                            "getUserCategoryRecordsOrderedByDateTimeDocument: $document"
                        )
                        val record = document.toObject<Record>()!!
                        val result = getDocuments(
                            record
                        )

                        val fromAccount = result.fromAccount.toObject<Account>()!!
                        val toAccount = result.toAccount.toObject<Account>()!!
                        val toCategory = result.toCategory.toObject<Category>()!!

                        TrueRecord(
                            record = record,
                            fromAccount = fromAccount,
                            toAccount = toAccount,
                            toCategory = toCategory
                        )
                    }
                    Log.e(
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
        startDate: Timestamp,
        endDate: Timestamp
    ) = callbackFlow<List<Record>> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserRecordsFromSpecificTime: $startDate\n:\n$endDate"
        )

        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordTimestamp", startDate
        ).whereLessThanOrEqualTo(
            "recordTimestamp", endDate
        ).whereEqualTo(
            "recordType",
            recordType
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    TAG,
                    "getUserRecordsFromSpecificTimeError: ${it.message}"
                )
                close(it)
                return@addSnapshotListener
            }

            value?.let { it ->
                val recordDocuments = it.documents
                val records: MutableMap<String, Record> = mutableMapOf()

                recordDocuments.forEach { document ->
                    val record = document.toObject<Record>()
                    if (record != null) {
                        val date = record.recordDateTime.toLocalDate().toString()
                        val currency = record.recordCurrency
                        val existingRecord = records[date]

                        if (existingRecord != null) {
                            records[date] = existingRecord.copy(
                                recordAmount = existingRecord.recordAmount + record.recordAmount
                            )
                        } else {
                            records[date] = record.copy(recordType = recordType)
                        }
                    }

                }

                val data = records.values.toList()
                Log.e(
                    TAG,
                    "getUserRecordsFromSpecificTimeData: "+data,

                )
                trySend(data)
            }
        }

        Log.e(
            TAG,
            "getUserRecordsFromSpecificTime0.0: babi"
        )

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserCategoriesWithAmountFromSpecificTime(
        categoryType: RecordType,
        startDate: Timestamp,
        endDate: Timestamp,
        currency: List<String>,
    ) = callbackFlow<List<CategoryWithAmount>> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserCategoriesWithAmountFromSpecificTime: $currency\n$categoryType\n$startDate\n:\n$endDate"
        )

        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordTimestamp", startDate
        ).whereLessThanOrEqualTo(
            "recordTimestamp", endDate
        ).whereEqualTo(
            "recordType",
            categoryType
        ).whereIn(
            "recordCurrency",
            currency.ifEmpty { listOf("") }
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    TAG,
                    "getUserCategoriesWithAmountFromSpecificTimeError: ${it.message}"
                )
                close(it)
            }

            value?.let { result ->
                Log.e(
                    TAG,
                    "getUserCategoriesWithAmountFromSpecificTimeResult: "+result,

                    )
                val recordDocuments = result.documents
                val categoriesWithAmount = mutableMapOf<String, CategoryWithAmount>()

                recordDocuments.forEach { document ->
                    val record = document.toObject<Record>()
                    if (record != null) {
                        val key = record.categoryIdFk+record.recordCurrency
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
                }

                launch {
                    val deferredCategoryDetails = categoriesWithAmount.map { (_, categoryWithAmount) ->
                        async {
                            val category = Firebase.firestore.collection(
                                "category"
                            ).document(categoryWithAmount.categoryId).get().await().toObject<Category>()
                            categoryWithAmount.copy(
                                category = category ?: Category()
                            )
                        }
                    }

                    val data = deferredCategoryDetails.awaitAll().sortedBy { it.amount }
                    Log.e(
                        TAG,
                        "getUserCategoriesWithAmountFromSpecificTimeData: "+data,

                        )
                    trySend(data)
                }
            }
        }
        Log.e(
            TAG,
            "getUserCategoriesWithAmountFromSpecificTime0.0: "+listener
        )
        awaitClose {
            listener.remove()
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        accountId: String,
    ) = callbackFlow<List<TrueRecord>> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserAccountRecordsOrderedByDateTime: " + accountId,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "accountIdFk", accountId
        ).whereIn(
            "accountfk", listOf(accountId)//????????????????????????????????????????????????????? should be or
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let { result ->
                val recordDocuments = result.documents
                launch {
                    val data = recordDocuments.map { document ->
                        Log.e(
                            TAG,
                            "getUserAccountRecordsOrderedByDateTime.1: $document"
                        )
                        val record = document.toObject<Record>()!!
                        Log.e(
                            TAG,
                            "getUserAccountRecordsOrderedByDateTime.2: $record"
                        )
                        val result = getDocuments(
                            record
                        )

                        val fromAccount = result.fromAccount.toObject<Account>()!!
                        val toAccount = result.toAccount.toObject<Account>()!!
                        val toCategory = result.toCategory.toObject<Category>()!!

                        TrueRecord(
                            record = record,
                            fromAccount = fromAccount,
                            toAccount = toAccount,
                            toCategory = toCategory
                        )
                    }
                    Log.e(
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
        Log.e(
            TAG,
            "getUserTotalAmountByType: " + recordType,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "recordType", recordType
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    TAG,
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.sumOf { document ->
                    document.toObject<Record>().recordAmount
                }
                Log.e(
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
        startDate: Timestamp,
        endDate: Timestamp
    ) = callbackFlow<Double> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserTotalAmountByTypeFromSpecificTime: " + recordType,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordTimestamp", startDate
        ).whereLessThanOrEqualTo(
            "recordTimestamp", endDate
        ).whereEqualTo(
            "recordType", recordType
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).orderBy(
            "recordTimestamp",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.sumOf { document ->
                    document.toObject<Record>().recordAmount
                }
                trySend(data)

            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalRecordBalance() = callbackFlow<Double> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserTotalRecordBalance: ",

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).whereNotEqualTo(
            "recordType", RecordType.Transfer
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.sumOf { document ->
                    document.toObject<Record>().recordAmount
                }
                Log.e(
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

    private suspend fun getDocuments(record: Record) = coroutineScope {
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

        DocumentResults(
            fromAccount = fromAccountDeferred.await(),
            toAccount = toAccountDeferred.await(),
            toCategory = toCategoryDeferred.await()
        )
    }

}

data class DocumentResults(
    val fromAccount: DocumentSnapshot,
    val toAccount: DocumentSnapshot,
    val toCategory: DocumentSnapshot,
)

data class TrueRecord(
    val record: Record = Record(),
    val fromAccount: Account = Account(),
    val toAccount: Account = Account(),
    val toCategory: Category = Category(),
)

data class CategoryWithAmount(
    val category: Category = Category(),
    val categoryId: String= "",
    val amount: Double = 0.0,
    val currency: String = ""
)
