package com.fredy.mysavings.Feature.Data.Database.FirestoreDataSource

import com.fredy.mysavings.Feature.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Util.Mappers.toTrueRecords
import com.fredy.mysavings.Util.Log
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

interface RecordDataSource {
    suspend fun upsertRecordItem(record: Record)
    suspend fun upsertAllRecordItem(records: List<Record>)
    suspend fun deleteRecordItem(record: Record)

    suspend fun deleteAllRecordItemInList(records: List<Record>)
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

    suspend fun getUserWalletRecordsOrderedByDateTime(
        userId: String,
        walletId: String,
    ): Flow<List<TrueRecord>>


    suspend fun getUserRecords(
        userId: String
    ): Flow<List<Record>>


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
        recordTypes: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>

//    suspend fun getUserWallets(userId: String): List<Wallet>
//    suspend fun getUserCategories(userId: String): List<Category>
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

    override suspend fun deleteAllRecordItemInList(records: List<Record>) {
        records.forEach { record ->
            recordCollection.document(record.recordId)
                .delete()
        }

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
                    "getUserTrueRecordFromSpecificTimeDS.Error: ${e.message}"
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
                    "getUserCategoryRecordsOrderedByDateTimeError: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserWalletRecordsOrderedByDateTime(
        userId: String,
        walletId: String,
    ): Flow<List<TrueRecord>> {
        val trueRecordComponentResult = getTrueRecordsComponent(
            userId
        )

        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = recordCollection.where(
                    Filter.or(
                        Filter.equalTo(
                            "walletIdFromFk", walletId
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
                    "getUserWalletRecordsOrderedByDateTimeError: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserRecords(
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
                    "getUserRecordsByType.Error: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordTypes: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            try {
                recordCollection.whereGreaterThanOrEqualTo(
                    "recordTimestamp",
                    TimestampConverter.fromDateTime(startDate)
                ).whereLessThanOrEqualTo(
                    "recordTimestamp",
                    TimestampConverter.fromDateTime(endDate)
                ).whereEqualTo(
                    "userIdFk", userId
                ).whereIn(
                    "recordType", recordTypes
                ).orderBy(
                    "recordTimestamp",
                    Query.Direction.DESCENDING
                ).snapshots().map { it.toObjects() }
            } catch (e: Exception) {
                Log.e("getUserRecordsByTypeFromSpecificTime.Error: ${e.message}")
                throw e
            }
        }
    }


    fun getUserWallets(
        userId: String
    ): Flow<List<Wallet>> {
        return Firebase.firestore.collection(
            "wallet"
        ).whereEqualTo(
            "userIdFk", userId
        ).snapshots().map { it.toObjects() }
    }

    fun getUserCategories(
        userId: String
    ): Flow<List<Category>> {
        return Firebase.firestore.collection(
            "category"
        ).whereEqualTo(
            "userIdFk", userId
        ).snapshots().map { it.toObjects() }
    }

    private suspend fun getTrueRecord(record: Record) = coroutineScope {
        withContext(Dispatchers.IO) {
            val fromWalletDeferred = async {
                Firebase.firestore.collection("wallet").document(
                    record.walletIdFromFk
                ).get().await()
            }

            val toWalletDeferred = async {
                Firebase.firestore.collection("wallet").document(
                    record.walletIdToFk
                ).get().await()
            }

            val toCategoryDeferred = async {
                Firebase.firestore.collection("category").document(
                    record.categoryIdFk
                ).get().await()
            }

            TrueRecord(
                record = record,
                fromWallet = fromWalletDeferred.await().toObject<Wallet>()!!,
                toWallet = toWalletDeferred.await().toObject<Wallet>()!!,
                toCategory = toCategoryDeferred.await().toObject<Category>()!!
            )
        }
    }

    private suspend fun getTrueRecordsComponent(
        userId: String
    ) = coroutineScope {
        withContext(Dispatchers.IO) {
            val userWallets = async {
                getUserWallets(userId).first()
            }

            val userCategories = async {
                getUserCategories(userId).first()
            }

            TrueRecordComponentResult(
                fromWallet = userWallets.await(),
                toWallet = userWallets.await(),
                toCategory = userCategories.await()
            )
        }
    }

    data class TrueRecordComponentResult(
        val fromWallet: List<Wallet>,
        val toWallet: List<Wallet>,
        val toCategory: List<Category>,
    )


//    suspend fun getUserTrueRecords(
//        userId: String,
//        startDate: LocalDateTime? = null,
//        endDate: LocalDateTime? = null,
//        categoryId: String? = null,
//        walletId: String? = null
//    ): Flow<List<TrueRecord>> {
//        val trueRecordComponentResult = getTrueRecordsComponent(userId)
//
//        return withContext(Dispatchers.IO) {
//            try {
//                var query = recordCollection.whereEqualTo("userIdFk", userId)
//
//                if (startDate != null && endDate != null) {
//                    query = query.whereGreaterThanOrEqualTo("recordTimestamp", TimestampConverter.fromDateTime(startDate))
//                        .whereLessThanOrEqualTo("recordTimestamp", TimestampConverter.fromDateTime(endDate))
//                }
//                if (categoryId != null) {
//                    query = query.whereEqualTo("categoryIdFk", categoryId)
//                }
//                if (walletId != null) {
//                    query = query.where(Filter.or(Filter.equalTo("walletIdFromFk", walletId), Filter.equalTo("walletIdToFk", walletId)))
//                }
//
//                query = query.orderBy("recordTimestamp", Query.Direction.DESCENDING)
//
//                val recordFlow = query.snapshots().map { it.toObjects<Record>() }
//                recordFlow.map { records ->
//                    records.toTrueRecords(trueRecordComponentResult)
//                }
//            } catch (e: Exception) {
//                Log.e("getUserTrueRecordsError: ${e.message}")
//                throw e
//            }
//        }
//    }

//    suspend fun getUserRecords(
//        userId: String,
//        startDate: LocalDateTime? = null,
//        endDate: LocalDateTime? = null,
//        recordType: RecordType? = null,
//        excludeTransfer: Boolean = false
//    ): Flow<List<Record>> {
//        return withContext(Dispatchers.IO) {
//            try {
//                var query = recordCollection.whereEqualTo("userIdFk", userId)
//
//                if (startDate != null && endDate != null) {
//                    query = query.whereGreaterThanOrEqualTo("recordTimestamp", TimestampConverter.fromDateTime(startDate))
//                        .whereLessThanOrEqualTo("recordTimestamp", TimestampConverter.fromDateTime(endDate))
//                }
//                if (recordType != null) {
//                    query = query.whereEqualTo("recordType", recordType)
//                }
//                if (excludeTransfer) {
//                    query = query.whereIn("recordType", listOf(RecordType.Expense, RecordType.Income))
//                }
//
//                query = query.orderBy("recordTimestamp", Query.Direction.DESCENDING)
//
//                val recordFlow = query.snapshots().map { it.toObjects<Record>() }
//                recordFlow
//            } catch (e: Exception) {
//                Log.e("getUserRecordsError: ${e.message}")
//                throw e
//            }
//        }
//    }

}
