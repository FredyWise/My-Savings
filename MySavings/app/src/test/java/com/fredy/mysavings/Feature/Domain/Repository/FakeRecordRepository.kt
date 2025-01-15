package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Record
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime


class FakeRecordRepository : RecordRepository {

    //    private val records = mutableListOf<Record>()
    val trueRecords = mutableListOf<TrueRecord>()

    override suspend fun upsertRecordItem(record: Record): String {
//        val existingRecord = records.find { it.recordId == record.recordId }
        val existingTrueRecord = trueRecords.find { it.record.recordId == record.recordId }
        return if (existingTrueRecord != null) {
//            records.remove(existingRecord)
//            records.add(record)
            trueRecords.remove(existingTrueRecord)
            trueRecords.add(TrueRecord(record))
            record.recordId
        } else {
            record.recordId.also {
//                records.add(record)
                trueRecords.add(TrueRecord(record))
            }
        }
    }

    override suspend fun upsertAllRecordItems(records: List<Record>) {
        this.trueRecords.addAll(records.map { TrueRecord(it) })
    }

    override suspend fun deleteRecordItem(record: Record) {
        val existingTrueRecord = trueRecords.find { it.record.recordId == record.recordId }
        trueRecords.remove(existingTrueRecord)
//        records.remove(record)
    }

    override suspend fun deleteAllRecordItems(records: List<Record>) {
        records.forEach {
            deleteRecordItem(it)
        }
    }

    override fun getUserRecords(userId: String): Flow<List<Record>> {
        return flow { emit(trueRecords.filter { it.record.userIdFk == userId }.map { it.record }) }
    }

    override suspend fun getRecordById(recordId: String): TrueRecord {
        return trueRecords.find { it.record.recordId == recordId }!!
    }

    override fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>> {
        return flow { emit(trueRecords.filter { it.record.userIdFk == userId && it.record.recordDateTime in startDate..endDate }) }
    }

    override fun getRecordMaps(userId: String): Flow<List<TrueRecord>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId })
        }
    }

     fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: SortType,
    ): Flow<List<TrueRecord>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && it.record.categoryIdFk == categoryId })
        }
    }

     fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
        sortType: SortType,
    ): Flow<List<TrueRecord>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && (it.record.walletIdToFk == accountId || it.record.walletIdFromFk == accountId) })
        }
    }

    override fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && it.record.recordType in recordType && it.record.recordDateTime in startDate..endDate }
                .map { it.record })
        }
    }

    override fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && it.record.recordDateTime in startDate..endDate }
                .map { it.record })
        }
    }

     fun getUserRecordsByType(
        userId: String,
        recordType: RecordType,
    ): Flow<List<Record>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && it.record.recordType == recordType }
                .map { it.record })
        }
    }
}



