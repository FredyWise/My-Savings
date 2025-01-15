package com.fredy.analysis.data

import com.fredy.analysis.domain.AnalysisRepository
import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Record
import com.fredy.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class AnalysisRepositoryImpl @Inject constructor(
    private val recordRepository: RecordRepository
//    private val recordDataSource: RecordDataSource,
//    private val recordDao: RecordDao,
) : AnalysisRepository {

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


    override fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return recordRepository.getUserRecordsByTypeFromSpecificTime(
            userId,
            recordType,
            startDate,
            endDate
        )
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

    override fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return recordRepository.getUserRecordsFromSpecificTime(
            userId, startDate, endDate
        )
//        Timber.i("getUserRecordsFromSpecificTimeRepo: $userId, $startDate, $endDate")
//        return flow {
//            recordDataSource.getUserRecordsFromSpecificTime(userId, startDate, endDate)
//                .collect { records ->
//                    Timber.i("getUserRecordsFromSpecificTimeRepo.Data: $records")
//                    emit(records.toDomainRecords())
//                }
//        }
    }


}