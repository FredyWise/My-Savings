package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Util.Mappers.toRecordSortedMaps
import com.fredy.mysavings.Feature.Domain.Model.RecordMap
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

    override  fun getUserRecords(userId: String): Flow<List<Record>> {
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

    override fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && it.record.categoryIdFk == categoryId }
                .toRecordSortedMaps(sortType))
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
        sortType: SortType,
    ): Flow<List<RecordMap>> {
        return flow {
            emit(trueRecords.filter { it.record.userIdFk == userId && (it.record.walletIdToFk == accountId || it.record.walletIdFromFk == accountId) }
                .toRecordSortedMaps())
        }
    }

    override fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return flow { emit(trueRecords.filter { it.record.userIdFk == userId && it.record.recordType in recordType && it.record.recordDateTime in startDate..endDate }.map { it.record }) }
    }

    override fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<Record>> {
        return flow { emit(trueRecords.filter { it.record.userIdFk == userId && it.record.recordDateTime in startDate..endDate }.map { it.record }) }
    }

    override fun getUserRecordsByType(
        userId: String,
        recordType: RecordType,
    ): Flow<List<Record>> {
        return flow { emit(trueRecords.filter { it.record.userIdFk == userId && it.record.recordType == recordType }.map { it.record }) }
    }
}



