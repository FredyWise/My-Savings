package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Mappers.toRecordSortedMaps
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Data.Database.Model.RecordMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject


interface RecordRepository {
    suspend fun upsertRecordItem(record: Record): String
    suspend fun upsertAllRecordItems(records: List<Record>)
    suspend fun deleteRecordItem(record: Record)
    suspend fun deleteAllRecordItems(records: List<Record>)
    suspend fun getRecordById(recordId: String): TrueRecord
    fun getRecordMaps(userId: String): Flow<List<TrueRecord>>
    fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>>

    fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>>

    fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>>

    fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>

    fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>

    fun getUserRecords(userId: String): Flow<List<Record>>

    fun getUserRecordsByType(userId: String, recordType: RecordType): Flow<List<Record>>

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
            Log.i("upsertRecordItemRepo: $record")
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
            Log.i("upsertAllRecordItemsRepo: $records")
            recordDataSource.upsertAllRecordItem(records)
            recordDao.upsertAllRecordItem(records)
        }
    }

    override suspend fun deleteRecordItem(
        record: Record
    ) {
        withContext(Dispatchers.IO) {
            Log.i("deleteRecordItem: $record")
            recordDataSource.deleteRecordItem(record)
            recordDao.deleteRecordItem(record)
        }
    }

    override suspend fun deleteAllRecordItems(records: List<Record>) {
        return withContext(Dispatchers.IO) {
            Log.i("upsertAllRecordItemsRepo: $records")
            recordDataSource.deleteAllRecordItemInList(records)
            recordDao.deleteAllRecordItemInList(records)
        }
    }

    override suspend fun getRecordById(recordId: String): TrueRecord {
        Log.i("getRecordByIdRepo: $recordId")
        return recordDataSource.getRecordById(
            recordId
        )
    }


    override fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>> {
        Log.i("getUserTrueRecordsFromSpecificTimeRepo: $userId, $startDate, $endDate")
        return flow {
            recordDataSource.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
                .collect {
                    Log.i("getUserTrueRecordsFromSpecificTimeRepo.Data: $it")
                    emit(it)
                }
        }
    }

    override fun getUserRecords(userId: String): Flow<List<Record>> {
        Log.i("getUserRecordsRepo: $userId")
        return flow {
            recordDataSource.getUserRecords(userId).collect {
                Log.i("getUserRecordsRepo.Data: $it")
                emit(it)
            }
        }

    }


    override fun getRecordMaps(userId: String): Flow<List<TrueRecord>> {
        Log.i("getRecordMapsRepo: $userId")
        return flow {
            recordDataSource.getUserTrueRecords(userId).collect { records ->
                Log.i("getRecordMapsRepo.Data: $records")
                emit(records)
            }
        }
    }

    override fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>> {
        Log.i("getUserCategoryRecordsOrderedByDateTimeRepo: $userId")
        return flow {
            recordDataSource.getUserCategoryRecordsOrderedByDateTime(userId, categoryId)
                .collect { records ->
                    Log.i("getUserCategoryRecordsOrderedByDateTimeRepo.Data: $records")
                    emit(records.toRecordSortedMaps())
                }
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>> {
        Log.i("getUserAccountRecordsOrderedByDateTimeRepo: $userId")
        return flow {
            recordDataSource.getUserAccountRecordsOrderedByDateTime(userId, accountId)
                .collect { records ->
                    Log.i("getUserAccountRecordsOrderedByDateTimeRepo.Data: $records")
                    emit(records.toRecordSortedMaps())
                }
        }
    }

    override fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        Log.i("getUserRecordsByTypeFromSpecificTimeRepo: $userId")
        return flow {
            recordDataSource.getUserRecordsByTypeFromSpecificTime(
                userId,
                recordType,
                startDate,
                endDate
            ).collect { records ->
                Log.i("getUserRecordsByTypeFromSpecificTimeRepo.Data: $records")
                emit(records)
            }
        }
    }

    override fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        Log.i("getUserRecordsFromSpecificTimeRepo: $userId, $startDate, $endDate")
        return flow {
            recordDataSource.getUserRecordsFromSpecificTime(userId, startDate, endDate)
                .collect { records ->
                    Log.i("getUserRecordsFromSpecificTimeRepo.Data: $records")
                    emit(records)
                }
        }
    }

    override fun getUserRecordsByType(
        userId: String,
        recordType: RecordType,
    ): Flow<List<Record>> {
        Log.i("getUserRecordsByTypeRepo: $userId")
        return flow {
            recordDataSource.getUserRecordsByType(userId, recordType).collect { records ->
                Log.i("getUserRecordsByTypeRepo.Data: $records")
                emit(records)
            }
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
