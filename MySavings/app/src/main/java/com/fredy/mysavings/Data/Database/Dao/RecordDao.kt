package com.fredy.mysavings.Data.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import java.time.LocalDateTime

@Dao
interface RecordDao {
    @Upsert
    suspend fun upsertRecordItem(recordItem: Record)

    @Delete
    suspend fun deleteRecordItem(recordItem: Record)

    @Query(
        "SELECT * FROM record " + "WHERE recordId = :id"
    )
    suspend fun getRecordById(id: String): TrueRecord

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND recordCurrency IN (:currency) AND recordTimestamp BETWEEN :start AND :end " + "ORDER BY recordTimestamp DESC"
    )
    suspend fun getUserTrueRecordByCurrencyFromSpecificTime(
        userId: String,
        start: LocalDateTime,
        end: LocalDateTime,
        currency: List<String>
    ): List<TrueRecord>

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND categoryIdFk = :categoryId " + "ORDER BY recordTimestamp DESC"
    )
    suspend fun getUserCategoryRecordsOrderedByDateTime(
        userId: String, categoryId: String
    ): List<TrueRecord>

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND (accountIdFromFk = :accountId OR accountIdToFk = :accountId) " + "ORDER BY recordTimestamp DESC"
    )
    suspend fun getUserAccountRecordsOrderedByDateTime(
        userId: String, accountId: String
    ): List<TrueRecord>

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND recordType = :recordType AND recordCurrency IN (:currency) AND recordTimestamp BETWEEN :start AND :end " + "ORDER BY recordTimestamp DESC"
    )
    suspend fun getUserRecordsByTypeAndCurrencyFromSpecificTime(
        userId: String,
        recordType: RecordType,
        start: LocalDateTime,
        end: LocalDateTime,
        currency: List<String>
    ): List<Record>
    @Query("SELECT * FROM record WHERE userIdFk = :userId")
    suspend fun getUserRecords(
        userId: String
    ): List<Record>

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND recordTimestamp BETWEEN :start AND :end " + "ORDER BY recordTimestamp DESC"
    )
    suspend fun getUserRecordsFromSpecificTime(
        userId: String,
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<Record>
    @Query("SELECT * FROM record WHERE userIdFk = :userId AND recordType = :recordType")
    suspend fun getUserRecordsByType(
        userId: String,
        recordType: RecordType
    ): List<Record>

    @Query("SELECT * FROM record WHERE userIdFk = :userId AND recordType = :recordType AND recordTimestamp BETWEEN :start AND :end")
    suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: RecordType,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Record>
}


