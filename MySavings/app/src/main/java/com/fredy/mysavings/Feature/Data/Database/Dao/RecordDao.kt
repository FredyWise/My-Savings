package com.fredy.mysavings.Feature.Data.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


@Dao
interface RecordDao {
    @Upsert
    suspend fun upsertRecordItem(recordItem: Record)

    @Upsert
    suspend fun upsertAllRecordItem(records: List<Record>)

    @Delete
    suspend fun deleteRecordItem(recordItem: Record)

    @Delete
    suspend fun deleteAllRecordItemInList(records: List<Record>)

    @Query("DELETE FROM record")
    suspend fun deleteAllRecords()

    @Query(
        "SELECT * FROM record " + "WHERE recordId = :id"
    )
    suspend fun getRecordById(id: String): TrueRecord

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND categoryIdFk = :categoryId " + "ORDER BY recordTimestamp DESC"
    )
    fun getUserCategoryRecordsOrderedByDateTime(
        userId: String, categoryId: String
    ): Flow<List<TrueRecord>>

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND (walletIdFromFk = :accountId OR walletIdToFk = :accountId) " + "ORDER BY recordTimestamp DESC"
    )
    fun getUserAccountRecordsOrderedByDateTime(
        userId: String, accountId: String
    ): Flow<List<TrueRecord>>

    @Query("SELECT * FROM record WHERE userIdFk = :userId")
    fun getUserRecords(
        userId: String
    ): Flow<List<Record>>

    @Query("SELECT * FROM record WHERE userIdFk = :userId")
    fun getUserTrueRecords(
        userId: String
    ): Flow<List<TrueRecord>>

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND recordTimestamp BETWEEN :start AND :end " + "ORDER BY recordTimestamp DESC"
    )
    fun getUserRecordsFromSpecificTime(
        userId: String,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Flow<List<Record>>

    @Query(
        "SELECT * FROM record " + "WHERE userIdFk = :userId AND recordTimestamp BETWEEN :start AND :end " + "ORDER BY recordTimestamp DESC"
    )
    fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Flow<List<TrueRecord>>

    @Query("SELECT * FROM record WHERE userIdFk = :userId AND recordType = :recordType")
    fun getUserRecordsByType(
        userId: String,
        recordType: RecordType
    ): Flow<List<Record>>

    @Query("SELECT * FROM record WHERE userIdFk = :userId AND recordType = :recordType AND recordTimestamp BETWEEN :start AND :end")
    fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: RecordType,
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<Record>>
}


