package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.RoomDatabase.Dao.RecordDao
import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.SavingsDatabase
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject


interface RecordRepository {
    suspend fun upsertRecordItem(recordItem: Record)
    suspend fun deleteRecordItem(recordItem: Record)
    fun getRecordById(id: Int): Flow<TrueRecord>
    fun getUserRecordsOrderedAscending(): Flow<List<Record>>
    fun getUserRecordsOrderedDescending(): Flow<List<TrueRecord>>
    fun getUserRecordsFromSpecificTime(
        start: LocalDateTime, end: LocalDateTime
    ): Flow<List<TrueRecord>>

    fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: Int
    ): Flow<List<TrueRecord>>

    fun getUserAccountRecordsOrderedByDateTime(
        accountId: Int
    ): Flow<List<TrueRecord>>

    fun getUserTotalAmountByType(recordType: RecordType): Flow<Double>
    fun getUserTotalAmountByTypeFromSpecificTime(recordType: RecordType, start: Int, end: Int): Flow<Double>
    fun getUserTotalRecordBalance(): Flow<Double>
}

class RecordRepositoryImpl @Inject constructor(
    private val savingsDatabase: SavingsDatabase,
): RecordRepository {
    override suspend fun upsertRecordItem(
        recordItem: Record
    ) {
        savingsDatabase.recordDao.upsertRecordItem(recordItem)
    }

    override suspend fun deleteRecordItem(
        recordItem: Record
    ) {
        savingsDatabase.recordDao.deleteRecordItem(recordItem)
    }

    override fun getRecordById(id: Int): Flow<TrueRecord> {
        return savingsDatabase.recordDao.getRecordById(id)
    }

    override fun getUserRecordsOrderedAscending(): Flow<List<Record>> {
        return savingsDatabase.recordDao.getUserRecordsOrderedAscending()
    }

    override fun getUserRecordsOrderedDescending(): Flow<List<TrueRecord>> {
        return savingsDatabase.recordDao.getUserRecordsOrderedDescending()
    }

    override fun getUserRecordsFromSpecificTime(
        start: LocalDateTime, end: LocalDateTime
    ): Flow<List<TrueRecord>> {
        return savingsDatabase.recordDao.getUserRecordsFromSpecificTime(
            start, end
        )
    }

    override fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: Int,
    ): Flow<List<TrueRecord>> {
        return savingsDatabase.recordDao.getUserCategoryRecordsOrderedByDateTime(
            categoryId
        )
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        accountId: Int,
    ): Flow<List<TrueRecord>> {
        return savingsDatabase.recordDao.getUserAccountRecordsOrderedByDateTime(
            accountId
        )
    }

    override fun getUserTotalAmountByType(
        recordType: RecordType
    ): Flow<Double> {
        return savingsDatabase.recordDao.getUserTotalAmountByType(
            recordType
        )
    }

    override fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        start: Int,
        end: Int
    ): Flow<Double> {
        return savingsDatabase.recordDao.getUserTotalAmountByTypeFromSpecificTime(
            recordType, start, end
        )
    }

    override fun getUserTotalRecordBalance(): Flow<Double> {
        return savingsDatabase.recordDao.getUserTotalRecordBalance()
    }
}