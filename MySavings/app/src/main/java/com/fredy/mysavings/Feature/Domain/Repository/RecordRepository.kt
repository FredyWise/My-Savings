package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.RecordMap
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


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


