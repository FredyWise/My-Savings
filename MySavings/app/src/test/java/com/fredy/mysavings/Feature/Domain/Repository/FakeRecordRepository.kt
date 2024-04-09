package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Mappers.toRecordSortedMaps
import com.fredy.mysavings.ViewModels.RecordMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime


class FakeRecordRepository : RecordRepository {

    private val records = mutableListOf<Record>()
    private val trueRecords = mutableListOf<TrueRecord>()

    override suspend fun upsertRecordItem(record: Record): String {
        val existingRecord = records.find { it.recordId == record.recordId }
        val existingTrueRecord = trueRecords.find { it.record.recordId == record.recordId }
        return if (existingRecord != null) {
            records.remove(existingRecord)
            records.add(record)
            trueRecords.remove(existingTrueRecord)
            trueRecords.add(TrueRecord(record))
            record.recordId
        } else {
            record.recordId.also {
                records.add(record)
                trueRecords.add(TrueRecord(record))
            }
        }
    }

    override suspend fun upsertAllRecordItems(records: List<Record>) {
        this.records.addAll(records)
    }

    override suspend fun deleteRecordItem(record: Record) {
        val existingTrueRecord = trueRecords.find { it.record.recordId == record.recordId }
        trueRecords.remove(existingTrueRecord)
        records.remove(record)
    }

    override suspend fun getUserRecords(userId: String): Flow<List<Record>> {
        return flow { emit(records.filter { it.userIdFk == userId }) }
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

    override suspend fun getRecordMaps(userId: String): Flow<List<RecordMap>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId }.toRecordSortedMaps())
        }
    }

    override suspend fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && it.record.categoryIdFk == categoryId }
                .toRecordSortedMaps(sortType))
        }
    }

    override suspend fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && (it.record.accountIdToFk == accountId || it.record.accountIdFromFk == accountId) }
                .toRecordSortedMaps())
        }
    }

    override suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return flow { emit(records.filter { it.userIdFk == userId && it.recordType in recordType && it.recordDateTime in startDate..endDate }) }
    }

    override suspend fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return flow { emit(records.filter { it.userIdFk == userId && it.recordDateTime in startDate..endDate }) }
    }

    override suspend fun getUserRecordsByType(
        userId: String,
        recordType: RecordType,
    ): Flow<List<Record>> {
        return flow { emit(records.filter { it.userIdFk == userId && it.recordType == recordType }) }
    }
}



