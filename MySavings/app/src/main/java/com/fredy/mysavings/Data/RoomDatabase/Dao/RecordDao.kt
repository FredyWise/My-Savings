package com.fredy.mysavings.Data.RoomDatabase.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Upsert
    suspend fun upsertRecordItem(recordItem: Record)
    @Delete
    suspend fun deleteRecordItem(recordItem: Record)

    @Query("SELECT * FROM record WHERE recordId = :id")
    fun getRecordById(id: Int): Flow<Record>

    @Query("SELECT * FROM record "+
            "ORDER BY recordDateTime DESC")
    fun getUserRecordsOrderedAscending(): Flow<List<Record>>

    @Query("SELECT * FROM record as r " +
            "INNER JOIN category AS c ON r.categoryIdFk = c.categoryId " +
            "INNER JOIN account AS a ON r.accountIdFromFk = a.accountId " +
            "ORDER BY recordDateTime DESC")
    fun getUserRecordsOrderedDescending(): Flow<List<TrueRecord>>

    @Query("SELECT * FROM record as r " +
            "INNER JOIN category AS c ON r.categoryIdFk = c.categoryId " +
            "INNER JOIN account AS a ON r.accountIdFromFk = a.accountId " +
            "WHERE r.recordDateTime > :start " +
            "AND r.recordDateTime < :end " +
            "ORDER BY recordDateTime ASC")
    fun getUserRecordsFromSpecificTime(start:Int,end: Int): Flow<List<TrueRecord>>

    @Query("SELECT * FROM record as r " +
            "INNER JOIN category AS c ON r.categoryIdFk = c.categoryId " +
            "INNER JOIN account AS a ON r.accountIdFromFk = a.accountId " +
            "WHERE c.categoryId=:categoryId " +
            "ORDER BY r.recordDateTime ASC")
    fun getUserCategoryRecordsOrderedByDateTime(categoryId: Int): Flow<List<TrueRecord>>

    @Query("SELECT * FROM record AS r " +
            "INNER JOIN category AS c ON r.categoryIdFk = c.categoryId " +
            "INNER JOIN account AS a1 ON r.accountIdFromFk = a1.accountId " +
            "WHERE r.accountIdFromFk=:accountId " +
            "ORDER BY r.recordDateTime ASC")
    fun getUserAccountRecordsOrderedByDateTime(accountId: Int): Flow<List<TrueRecord>>

    @Query("SELECT recordAmount FROM record ")
    fun getUserTotalBalance(): Flow<List<Double>>

    @Query("SELECT SUM(recordAmount) FROM record "+
            "WHERE recordDateTime > :start " +
            "AND recordDateTime < :end " +
            "ORDER BY recordDateTime ASC")
    fun getUserTotalBalanceFromSpecificTime(start:Int,end: Int): Flow<Double>

    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordAmount < 0 ")
    fun getUserTotalExpenses(): Flow<Double>

    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordAmount < 0 "+
            "AND recordDateTime > :start " +
            "AND recordDateTime < :end " +
            "ORDER BY recordDateTime ASC")
    fun getUserTotalExpensesFromSpecificTime(start:Int,end: Int): Flow<Double>

    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordAmount >= 0 ")
    fun getUserTotalIncomes(): Flow<Double>

    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordAmount >= 0 "+
            "AND recordDateTime > :start " +
            "AND recordDateTime < :end " +
            "ORDER BY recordDateTime ASC")
    fun getUserTotalIncomesFromSpecificTime(start:Int,end: Int): Flow<Double>
}

data class TrueRecord(
    @Embedded val record: Record,
    @Embedded val fromAccount: Account,
    @Embedded val toCategory: Category,
)
