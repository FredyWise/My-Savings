package com.fredy.book.data

import com.fredy.book.domain.RecordSpecificRepository
import com.fredy.data.database.dao.RecordDao
import com.fredy.data.database.firestoreDataSource.RecordDataSource
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

class RecordSpecificRepositoryImpl @Inject constructor(
//    private val recordDataSource: RecordDataSource,
//    private val recordDao: RecordDao,
    private val recordRepository: RecordRepository
) : RecordSpecificRepository {


    override suspend fun deleteRecordItem(
        record: Record
    ) {
        recordRepository.deleteRecordItem(record)
//        withContext(Dispatchers.IO) {
//            Timber.i("deleteRecordItem: $record")
//            val dataRecord = record.toDataRecord()
//            recordDataSource.deleteRecordItem(dataRecord)
//            recordDao.deleteRecordItem(dataRecord)
//        }
    }

    override fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>> {
        return recordRepository.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
//        Timber.i("getUserTrueRecordsFromSpecificTimeRepo: $userId, $startDate, $endDate")
//        return flow {
//            recordDataSource.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
//                .collect { data ->
//                    Timber.i("getUserTrueRecordsFromSpecificTimeRepo.Data: $data")
//                    emit(data.toDomainTrueRecords())
//                }
//        }
    }

    override fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return recordRepository.getUserRecordsByTypeFromSpecificTime(userId, recordType, startDate, endDate)
//        Timber.i("getUserRecordsByTypeFromSpecificTimeRepo: $userId")
//        return flow {
//            recordDataSource.getUserRecordsByTypeFromSpecificTime(
//                userId,
//                recordType,
//                startDate,
//                endDate
//            ).collect { records ->
//                Timber.i("getUserRecordsByTypeFromSpecificTimeRepo.Data: $records")
//                emit(records.toDomainRecords())
//            }
//        }
    }


}