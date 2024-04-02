package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Mappers.toTrueRecords
import com.fredy.mysavings.Util.TAG
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

interface RecordDataSource {
    suspend fun upsertRecordItem(record: Record)
    suspend fun deleteRecordItem(record: Record)
    suspend fun upsertAllRecordItem(records: List<Record>)
    suspend fun getRecordById(recordId: String): TrueRecord
    suspend fun getUserTrueRecords(
        userId: String,
    ): Flow<List<TrueRecord>>

    suspend fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>>

    suspend fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
    ): Flow<List<TrueRecord>>

    suspend fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
    ): Flow<List<TrueRecord>>


    suspend fun getUserFlowOfRecords(
        userId: String
    ): Flow<List<Record>>

    suspend fun getUserRecords(
        userId: String
    ): List<Record>

    suspend fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>>

    suspend fun getUserRecordsByType(
        userId: String, recordType: RecordType
    ): Flow<List<Record>>

    suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>

    suspend fun getUserRecordsByTypeNotEqualTransferFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>

    suspend fun getUserAccounts(userId: String): List<Account>
    suspend fun getUserCategories(userId: String): List<Category>
}

class RecordDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RecordDataSource {
    private val recordCollection = firestore.collection(
        "record"
    )

    override suspend fun upsertRecordItem(
        record: Record
    ) {
        recordCollection.document(
            record.recordId
        ).set(
            record
        )
    }


    override suspend fun upsertAllRecordItem(records: List<Record>) {
        val batch = firestore.batch()
        for (record in records) {
            batch.set(recordCollection.document(record.recordId), record)
        }
        batch.commit()
    }

    override suspend fun deleteRecordItem(
        record: Record
    ) {
        recordCollection.document(
            record.recordId
        ).delete()
    }

    override suspend fun getRecordById(recordId: String): TrueRecord {
        return withContext(Dispatchers.IO) {
            try {
                val recordSnapshot = recordCollection.document(
                    recordId
                ).get().await()
                val record = recordSnapshot.toObject<Record>() ?: throw Exception(
                    "Record Not Found"
                )
                getTrueRecord(
                    record
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Failed to get record: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserTrueRecords(
        userId: String,
    ): Flow<List<TrueRecord>> {
        val trueRecordComponentResult = getTrueRecordsComponent(
            userId
        )

        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = recordCollection.whereEqualTo(
                    "userIdFk", userId
                ).orderBy(
                    "recordTimestamp",
                    Query.Direction.DESCENDING
                ).snapshots()
                val recordFlow = querySnapshot.map { it.toObjects<Record>() }
                recordFlow.map { records ->
                    records.toTrueRecords(trueRecordComponentResult)
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserTrueRecordFromSpecificTimeError: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>> {
        val trueRecordComponentResult = getTrueRecordsComponent(
            userId
        )
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = recordCollection.whereGreaterThanOrEqualTo(
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
                    "userIdFk", userId
                ).orderBy(
                    "recordTimestamp",
                    Query.Direction.DESCENDING
                ).snapshots()
                val recordFlow = querySnapshot.map { it.toObjects<Record>() }
                recordFlow.map { records ->
                    records.toTrueRecords(trueRecordComponentResult)
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserTrueRecordFromSpecificTimeError: ${e.message}"
                )
                throw e
            }
        }
    }


    override suspend fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
    ): Flow<List<TrueRecord>> {
        val trueRecordComponentResult = getTrueRecordsComponent(
            userId
        )

        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = recordCollection.whereEqualTo(
                    "categoryIdFk", categoryId
                ).whereEqualTo(
                    "userIdFk", userId
                ).orderBy(
                    "recordTimestamp",
                    Query.Direction.DESCENDING
                ).snapshots()
                val recordFlow = querySnapshot.map { it.toObjects<Record>() }
                recordFlow.map { records ->
                    records.toTrueRecords(trueRecordComponentResult)
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserCategoryRecordsOrderedByDateTimeError: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
    ): Flow<List<TrueRecord>> {
        val trueRecordComponentResult = getTrueRecordsComponent(
            userId
        )

        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = recordCollection.where(
                    Filter.or(
                        Filter.equalTo(
                            "accountIdFromFk",
                            accountId
                        ), Filter.equalTo(
                            "accountIdToFk", accountId
                        )
                    )
                ).whereEqualTo(
                    "userIdFk", userId
                ).orderBy(
                    "recordTimestamp",
                    Query.Direction.DESCENDING
                ).snapshots()
                val recordFlow = querySnapshot.map { it.toObjects<Record>() }
                recordFlow.map { records ->
                    records.toTrueRecords(trueRecordComponentResult)
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserAccountRecordsOrderedByDateTimeError: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserFlowOfRecords(
        userId: String
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = recordCollection.whereEqualTo(
                    "userIdFk", userId
                ).snapshots()

                querySnapshot.map { it.toObjects() }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserTotalAmountByTypeError: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = recordCollection.whereGreaterThanOrEqualTo(
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
                    "userIdFk", userId
                ).orderBy(
                    "recordTimestamp",
                    Query.Direction.DESCENDING
                ).snapshots()
                querySnapshot.map { it.toObjects() }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserRecordsFromSpecificTime.Error: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserRecordsByType(
        userId: String, recordType: RecordType
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = recordCollection.whereEqualTo(
                    "recordType", recordType
                ).whereEqualTo(
                    "userIdFk", userId
                ).snapshots()
                querySnapshot.map { it.toObjects() }

            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserRecordsByType.Error: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            try {
                recordCollection.whereGreaterThanOrEqualTo(
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
                    "userIdFk", userId
                ).whereEqualTo(
                    "recordType", recordType
                ).orderBy(
                    "recordTimestamp",
                    Query.Direction.DESCENDING
                ).snapshots().map { it.toObjects() }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserRecordsByTypeFromSpecificTime.Error: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserRecordsByTypeNotEqualTransferFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            try {
                recordCollection.whereGreaterThanOrEqualTo(
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
                    "userIdFk", userId
                ).whereIn(
                    "recordType", listOf(RecordType.Expense, RecordType.Income)
                ).orderBy(
                    "recordTimestamp",
                    Query.Direction.DESCENDING
                ).snapshots().map { it.toObjects() }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "getUserRecordsByTypeFromSpecificTime.Error: ${e.message}"
                )
                throw e
            }
        }
    }

    private val _records = MutableLiveData<List<Record>>()
    private val _accounts = MutableLiveData<List<Account>>()
    private val _categories = MutableLiveData<List<Category>>()


    override suspend fun getUserRecords(
        userId: String
    ): List<Record> {
        val records = _records.value ?: recordCollection.whereEqualTo(
            "userIdFk", userId
        ).get().await().toObjects()
        _records.postValue(records)
        return records
    }

    override suspend fun getUserAccounts(
        userId: String
    ): List<Account> {
        val accounts = _accounts.value ?: (Firebase.firestore.collection(
            "account"
        ).whereEqualTo(
            "userIdFk", userId
        ).get().await().toObjects<Account>() + Firebase.firestore.collection(
            "account"
        ).whereEqualTo(
            "userIdFk", "0"
        ).get().await().toObjects<Account>())
        _accounts.postValue(accounts)
        return accounts
    }

    override suspend fun getUserCategories(
        userId: String
    ) : List<Category> {
        val categories = _categories.value ?: (Firebase.firestore.collection(
            "category"
        ).whereEqualTo(
            "userIdFk", userId
        ).get().await().toObjects<Category>() + Firebase.firestore.collection(
            "category"
        ).whereEqualTo(
            "userIdFk", "0"
        ).get().await().toObjects<Category>())
        _categories.postValue(categories)
        return categories
    }

    private suspend fun getTrueRecord(record: Record) = coroutineScope {
        withContext(Dispatchers.IO) {
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
    }

    private suspend fun getTrueRecordsComponent(
        userId: String
    ) = coroutineScope {
        withContext(Dispatchers.IO) {
            val fromAccountDeferred = async {
                getUserAccounts(userId)
            }

            val toAccountDeferred = async {
                getUserAccounts(userId)
            }

            val toCategoryDeferred = async {
                getUserCategories(userId)
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

}
