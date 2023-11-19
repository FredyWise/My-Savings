package com.fredy.mysavings.Data.RoomDatabase.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface RecordDao {
    @Upsert
    suspend fun upsertRecordItem(recordItem: Record)
    @Delete
    suspend fun deleteRecordItem(recordItem: Record)

    @Query("SELECT * FROM record " +
            "WHERE recordId = :id")
    fun getRecordById(id: Int): Flow<TrueRecord>

    @Query("SELECT * FROM record "+
            "ORDER BY recordDateTime DESC")
    fun getUserRecordsOrderedAscending(): Flow<List<Record>> //x

    @Query("SELECT * FROM record")
    fun getUserRecordsOrderedDescending(): Flow<List<TrueRecord>> //x

    @Query("SELECT * FROM record "+
            "WHERE recordDateTime BETWEEN :start AND :end " +
            "ORDER BY recordDateTime DESC")
    fun getUserRecordsFromSpecificTime(start: LocalDateTime,end: LocalDateTime): Flow<List<TrueRecord>>

    @Query("SELECT * FROM record as r " +
            "WHERE categoryIdFk=:categoryId")
    fun getUserCategoryRecordsOrderedByDateTime(categoryId: Int): Flow<List<TrueRecord>>

    @Query("SELECT * FROM record "+
            "WHERE accountIdFromFk=:accountId " +
            "OR accountIdToFk=:accountId")
    fun getUserAccountRecordsOrderedByDateTime(accountId: Int): Flow<List<TrueRecord>>


    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordType=:recordType")
    fun getUserTotalAmountByType(recordType: RecordType): Flow<Double>

    @Query("SELECT SUM(recordAmount) FROM record "+
            "WHERE recordType=:recordType AND recordDateTime > :start " +
            "AND recordDateTime < :end ")
    fun getUserTotalAmountByTypeFromSpecificTime(recordType: RecordType,start:Int,end: Int): Flow<Double>

    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordType " +
            "IN ('Income', 'Expense')")
    fun getUserTotalRecordBalance(): Flow<Double>

    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordAmount < 0 "+
            "AND recordDateTime > :start " +
            "AND recordDateTime < :end " +
            "ORDER BY recordDateTime ASC")
    fun getUserTotalExpensesFromSpecificTime(start:Int,end: Int): Flow<Double> //x

    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordAmount >= 0 ")
    fun getUserTotalIncomes(): Flow<Double> //x

    @Query("SELECT SUM(recordAmount) FROM record " +
            "WHERE recordAmount >= 0 "+
            "AND recordDateTime > :start " +
            "AND recordDateTime < :end " +
            "ORDER BY recordDateTime ASC")
    fun getUserTotalIncomesFromSpecificTime(start:Int,end: Int): Flow<Double> //x
}

data class TrueRecord(
    @Embedded val record: Record = Record(),
    @Relation(
        parentColumn = "accountIdFromFk",
        entityColumn = "accountId"
    ) val fromAccount: Account = Account(),
    @Relation(
        parentColumn = "accountIdToFk",
        entityColumn = "accountId"
    ) val toAccount: Account = Account(),
    @Relation(
        parentColumn = "categoryIdFk",
        entityColumn = "categoryId"
    ) val toCategory: Category = Category(),
)
