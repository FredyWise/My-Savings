package com.fredy.mysavings.Feature.Domain.Repository

import android.util.Log
import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Mappers.toRecordSortedMaps
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.RecordMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject


interface RecordRepository {
    suspend fun upsertRecordItem(record: Record): String
    suspend fun upsertAllRecordItems(records: List<Record>)
    suspend fun deleteRecordItem(record: Record)
    suspend fun getRecordById(recordId: String): TrueRecord
    suspend fun getRecordMaps(userId: String): Flow<List<RecordMap>>
    suspend fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>>

    suspend fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>>

   suspend fun getUserAccountRecordsOrderedByDateTime(
       userId: String,
        accountId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>>

    suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>

    suspend fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>

    suspend fun getUserRecords(userId: String): Flow<List<Record>>

    suspend fun getUserRecordsByType(userId: String, recordType: RecordType): Flow<List<Record>>

}

class RecordRepositoryImpl @Inject constructor(
    private val recordDataSource: RecordDataSource,
    private val recordDao: RecordDao,
    private val firestore: FirebaseFirestore,
) : RecordRepository {
    private val recordCollection = firestore.collection(
        "record"
    )

    override suspend fun upsertRecordItem(
        record: Record
    ): String {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, "upsertRecordItem: $record")
            val tempRecord = if (record.recordId.isEmpty()) {
                val newRecordRef = recordCollection.document()
                record.copy(
                    recordId = newRecordRef.id
                )
            } else {
                record
            }

            recordDao.upsertRecordItem(tempRecord)
            recordDataSource.upsertRecordItem(
                tempRecord
            )
            tempRecord.recordId
        }
    }

    override suspend fun upsertAllRecordItems(records: List<Record>) {
        return withContext(Dispatchers.IO) {
            recordDataSource.upsertAllRecordItem(records)
            recordDao.upsertAllRecordItem(records)
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

    override suspend fun getUserRecords(userId: String): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            recordDataSource.getUserRecordsFlow(userId)
        }
    }


    override suspend fun getRecordById(recordId: String): TrueRecord {
        Log.i(TAG, "getRecordByIdRepo: $recordId")
        return withContext(Dispatchers.IO) {
            recordDataSource.getRecordById(
                recordId
            )
        }
    }

    override suspend fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>> {
        return withContext(Dispatchers.IO) {
            recordDataSource.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
        }
    }

    override suspend fun getRecordMaps(userId: String): Flow<List<RecordMap>> {
        return withContext(Dispatchers.IO) {
            recordDataSource.getUserTrueRecords(userId).map { records ->
                records.toRecordSortedMaps()
            }
        }
    }

    override suspend fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>> {
        return withContext(Dispatchers.IO) {
            recordDataSource.getUserCategoryRecordsOrderedByDateTime(
                userId, categoryId
            ).map { records ->
                records.toRecordSortedMaps()
            }
        }
    }

    override suspend fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>> {
        return withContext(Dispatchers.IO) {
            recordDataSource.getUserAccountRecordsOrderedByDateTime(
                userId, accountId
            ).map { records ->
                records.toRecordSortedMaps()
            }
        }
    }

    override suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            recordDataSource.getUserRecordsByTypeFromSpecificTime(
                userId,
                recordType,
                startDate,
                endDate,
            )
        }
    }

    override suspend fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            recordDataSource.getUserRecordsFromSpecificTime(
                userId, startDate, endDate
            )
        }
    }

    override suspend fun getUserRecordsByType(
        userId: String,
        recordType: RecordType,
    ): Flow<List<Record>> {
        return withContext(Dispatchers.IO) {
            recordDataSource.getUserRecordsByType(
                userId, recordType
            )
        }
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
