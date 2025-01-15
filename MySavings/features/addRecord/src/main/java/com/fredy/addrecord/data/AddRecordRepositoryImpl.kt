package com.fredy.addrecord.data

import com.fredy.addrecord.domain.AddRecordRepository
import com.fredy.domain.model.Record
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.RecordRepository
import javax.inject.Inject

class AddRecordRepositoryImpl @Inject constructor(
    private val recordRepository: RecordRepository
//    private val recordDataSource: RecordDataSource,
//    private val recordDao: RecordDao,
//    private val firestore: FirebaseFirestore,
) : AddRecordRepository {
//    private val recordCollection = firestore.collection(
//        "record"
//    )

    override suspend fun upsertRecordItem(
        record: Record
    ): String {
        return recordRepository.upsertRecordItem(record)
//        return withContext(Dispatchers.IO) {
//            Timber.i("upsertRecordItemRepo: $record")
//            val tempRecord = if (record.recordId.isEmpty()) {
//                val newRecordRef = recordCollection.document()
//                record.copy(
//                    recordId = newRecordRef.id
//                )
//            } else {
//                record
//            }.toDataRecord()
//
//            recordDao.upsertRecordItem(tempRecord)
//            recordDataSource.upsertRecordItem(
//                tempRecord
//            )
//            tempRecord.recordId
//        }
    }

    override suspend fun upsertAllRecordItems(records: List<Record>) {
        recordRepository.upsertAllRecordItems(records)
//         withContext(Dispatchers.IO) {
//            Timber.i("upsertAllRecordItemsRepo: $records")
//            val dataRecords = records.map { record ->
//                if (record.recordId.isEmpty()) {
//                    val newRecordRef = recordCollection.document()
//                    record.copy(
//                        recordId = newRecordRef.id
//                    )
//                } else {
//                    record
//                }
//            }.toDataRecords()
//            recordDataSource.upsertAllRecordItem(dataRecords)
//            recordDao.upsertAllRecordItem(dataRecords)
//        }
    }


    override suspend fun getRecordById(recordId: String): TrueRecord {
        return recordRepository.getRecordById(recordId)
//        Timber.i("getRecordByIdRepo: $recordId")
//        return recordDataSource.getRecordById(
//            recordId
//        ).toDomainTrueRecord()
    }


}