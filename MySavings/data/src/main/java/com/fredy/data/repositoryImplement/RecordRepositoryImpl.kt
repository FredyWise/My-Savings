package com.fredy.data.repositoryImplement

import com.fredy.data.database.dao.RecordDao
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.data.mappers.toDataRecord
import com.fredy.data.mappers.toDataRecords
import com.fredy.data.mappers.toDomainRecords
import com.fredy.data.mappers.toDomainTrueRecord
import com.fredy.data.mappers.toDomainTrueRecords
import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Record
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.RecordRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

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
            }.toDataRecord()

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
            val dataRecords = records.map { record ->
                if (record.recordId.isEmpty()) {
                    val newRecordRef = recordCollection.document()
                    record.copy(
                        recordId = newRecordRef.id
                    )
                } else {
                    record
                }
            }.toDataRecords()
            recordDataSource.upsertAllRecordItem(dataRecords)
            recordDao.upsertAllRecordItem(dataRecords)
        }
    }

    override suspend fun deleteRecordItem(
        record: Record
    ) {
        withContext(Dispatchers.IO) {
            Timber.i("deleteRecordItem: $record")
            val dataRecord = record.toDataRecord()
            recordDataSource.deleteRecordItem(dataRecord)
            recordDao.deleteRecordItem(dataRecord)
        }
    }

    override suspend fun deleteAllRecordItems(records: List<Record>) {
        return withContext(Dispatchers.IO) {
            Timber.i("upsertAllRecordItemsRepo: $records")
            val dataRecords = records.toDataRecords()
            recordDataSource.deleteAllRecordItemInList(dataRecords)
            recordDao.deleteAllRecordItemInList(dataRecords)
        }
    }

    override suspend fun getRecordById(recordId: String): TrueRecord {
        Timber.i("getRecordByIdRepo: $recordId")
        return recordDataSource.getRecordById(
            recordId
        ).toDomainTrueRecord()
    }


    override fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>> {
        Timber.i("getUserTrueRecordsFromSpecificTimeRepo: $userId, $startDate, $endDate")
        return flow {
            recordDataSource.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
                .collect { data ->
                    Timber.i("getUserTrueRecordsFromSpecificTimeRepo.Data: $data")
                    emit(data.toDomainTrueRecords())
                }
        }
    }

    override fun getUserRecords(userId: String): Flow<List<Record>> {
        Timber.i("getUserRecordsRepo: $userId")
        return flow {
            recordDataSource.getUserRecords(userId).collect {
                Timber.i("getUserRecordsRepo.Data: $it")
                emit(it.toDomainRecords())
            }
        }

    }


    override fun getRecordMaps(userId: String): Flow<List<TrueRecord>> {
        Timber.i("getRecordMapsRepo: $userId")
        return flow {
            recordDataSource.getUserTrueRecords(userId).collect { records ->
                Timber.i("getRecordMapsRepo.Data: $records")
                emit(records.toDomainTrueRecords())
            }
        }
    }

    override fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: com.fredy.domain.enums.SortType,
    ): Flow<List<TrueRecord>> {
        Timber.i("getUserCategoryRecordsOrderedByDateTimeRepo: $userId")
        return flow {
            recordDataSource.getUserCategoryRecordsOrderedByDateTime(userId, categoryId)
                .collect { records ->
                    Timber.i("getUserCategoryRecordsOrderedByDateTimeRepo.Data: $records")
                    emit(records.toDomainTrueRecords())
                }
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
        sortType: com.fredy.domain.enums.SortType,
    ): Flow<List<TrueRecord>> {
        Timber.i("getUserAccountRecordsOrderedByDateTimeRepo: $accountId")
        return flow {
            recordDataSource.getUserWalletRecordsOrderedByDateTime(userId, accountId)
                .collect { records ->
                    Timber.i("getUserAccountRecordsOrderedByDateTimeRepo.Data: $records")
                    emit(records.toDomainTrueRecords())
                }
        }
    }

    override fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
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
                emit(records.toDomainRecords())
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
                    emit(records.toDomainRecords())
                }
        }
    }

    override fun getUserRecordsByType(
        userId: String,
        recordType: RecordType,
    ): Flow<List<Record>> {
        Timber.i("getUserRecordsByTypeRepo: $userId")
        return flow {
            recordDataSource.getUserRecordsByType(userId, recordType).collect { records ->
                Timber.i("getUserRecordsByTypeRepo.Data: $records")
                emit(records.toDomainRecords())
            }
        }
    }

}