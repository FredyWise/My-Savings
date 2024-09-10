package com.fredy.data.repositoryImplement

import com.fredy.data.database.dao.RecordDao
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.RecordMap
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.util.mappers.toRecordSortedMaps
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject
import com.fredy.domain.model.Record

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
            Timber.i("upsertRecordItemRepo: $record")
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
            Timber.i("upsertAllRecordItemsRepo: $records")
            val temRecords = records.map {record ->
                if (record.recordId.isEmpty()) {
                    val newRecordRef = recordCollection.document()
                    record.copy(
                        recordId = newRecordRef.id
                    )
                } else {
                    record
                }
            }
            recordDataSource.upsertAllRecordItem(temRecords)
            recordDao.upsertAllRecordItem(temRecords)
        }
    }

    override suspend fun deleteRecordItem(
        record: Record
    ) {
        withContext(Dispatchers.IO) {
            Timber.i("deleteRecordItem: $record")
            recordDataSource.deleteRecordItem(record)
            recordDao.deleteRecordItem(record)
        }
    }

    override suspend fun deleteAllRecordItems(records: List<Record>) {
        return withContext(Dispatchers.IO) {
            Timber.i("upsertAllRecordItemsRepo: $records")
            recordDataSource.deleteAllRecordItemInList(records)
            recordDao.deleteAllRecordItemInList(records)
        }
    }

    override suspend fun getRecordById(recordId: String): TrueRecord {
        Timber.i("getRecordByIdRepo: $recordId")
        return recordDataSource.getRecordById(
            recordId
        )
    }


    override fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>> {
        Timber.i("getUserTrueRecordsFromSpecificTimeRepo: $userId, $startDate, $endDate")
        return flow {
            recordDataSource.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
                .collect {
                    Timber.i("getUserTrueRecordsFromSpecificTimeRepo.Data: $it")
                    emit(it)
                }
        }
    }

    override fun getUserRecords(userId: String): Flow<List<Record>> {
        Timber.i("getUserRecordsRepo: $userId")
        return flow {
            recordDataSource.getUserRecords(userId).collect {
                Timber.i("getUserRecordsRepo.Data: $it")
                emit(it)
            }
        }

    }


    override fun getRecordMaps(userId: String): Flow<List<TrueRecord>> {
        Timber.i("getRecordMapsRepo: $userId")
        return flow {
            recordDataSource.getUserTrueRecords(userId).collect { records ->
                Timber.i("getRecordMapsRepo.Data: $records")
                emit(records)
            }
        }
    }

    override fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: com.fredy.domain.enums.SortType,
    ): Flow<List<RecordMap>> {
        Timber.i("getUserCategoryRecordsOrderedByDateTimeRepo: $userId")
        return flow {
            recordDataSource.getUserCategoryRecordsOrderedByDateTime(userId, categoryId)
                .collect { records ->
                    Timber.i("getUserCategoryRecordsOrderedByDateTimeRepo.Data: $records")
                    emit(records.toRecordSortedMaps())
                }
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
        sortType: com.fredy.domain.enums.SortType,
    ): Flow<List<RecordMap>> {
        Timber.i("getUserAccountRecordsOrderedByDateTimeRepo: $accountId")
        return flow {
            recordDataSource.getUserWalletRecordsOrderedByDateTime(userId, accountId)
                .collect { records ->
                    Timber.i("getUserAccountRecordsOrderedByDateTimeRepo.Data: $records")
                    emit(records.toRecordSortedMaps())
                }
        }
    }

    override fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<com.fredy.domain.enums.RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        Timber.i("getUserRecordsByTypeFromSpecificTimeRepo: $userId")
        return flow {
            recordDataSource.getUserRecordsByTypeFromSpecificTime(
                userId,
                recordType,
                startDate,
                endDate
            ).collect { records ->
                Timber.i("getUserRecordsByTypeFromSpecificTimeRepo.Data: $records")
                emit(records)
            }
        }
    }

    override fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        Timber.i("getUserRecordsFromSpecificTimeRepo: $userId, $startDate, $endDate")
        return flow {
            recordDataSource.getUserRecordsFromSpecificTime(userId, startDate, endDate)
                .collect { records ->
                    Timber.i("getUserRecordsFromSpecificTimeRepo.Data: $records")
                    emit(records)
                }
        }
    }

    override fun getUserRecordsByType(
        userId: String,
        recordType: com.fredy.domain.enums.RecordType,
    ): Flow<List<Record>> {
        Timber.i("getUserRecordsByTypeRepo: $userId")
        return flow {
            recordDataSource.getUserRecordsByType(userId, recordType).collect { records ->
                Timber.i("getUserRecordsByTypeRepo.Data: $records")
                emit(records)
            }
        }
    }

}