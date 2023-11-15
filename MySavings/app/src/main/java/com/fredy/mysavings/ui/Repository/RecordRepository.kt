package com.fredy.mysavings.ui.Repository

import com.fredy.mysavings.Data.RoomDatabase.Dao.RecordDao
import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import kotlinx.coroutines.flow.Flow


interface RecordRepository {
    suspend fun upsertRecordItem(recordItem: Record)
    suspend fun deleteRecordItem(recordItem: Record)
    fun getRecordById(id: Int): Flow<TrueRecord>
    fun getUserRecordsOrderedAscending(): Flow<List<Record>>
    fun getUserRecordsOrderedDescending(): Flow<List<TrueRecord>>
    fun getUserRecordsFromSpecificTime(start: Int, end: Int): Flow<List<TrueRecord>>
    fun getUserCategoryRecordsOrderedByDateTime(categoryId: Int): Flow<List<TrueRecord>>
    fun getUserAccountRecordsOrderedByDateTime(accountId: Int): Flow<List<TrueRecord>>
    fun getUserTotalAmountByType(recordType: RecordType): Flow<Double>
}

class RecordRepositoryImpl(
    private val recordDao: RecordDao,
) : RecordRepository {
    override suspend fun upsertRecordItem(recordItem: Record) {
        recordDao.upsertRecordItem(recordItem)
    }

    override suspend fun deleteRecordItem(recordItem: Record) {
        recordDao.deleteRecordItem(recordItem)
    }

    override fun getRecordById(id: Int): Flow<TrueRecord> {
        return recordDao.getRecordById(id)
    }

    override fun getUserRecordsOrderedAscending(): Flow<List<Record>> {
        return recordDao.getUserRecordsOrderedAscending()
    }

    override fun getUserRecordsOrderedDescending(): Flow<List<TrueRecord>> {
        return recordDao.getUserRecordsOrderedDescending()
    }

    override fun getUserRecordsFromSpecificTime(
        start: Int,
        end: Int
    ): Flow<List<TrueRecord>> {
        return recordDao.getUserRecordsFromSpecificTime(start, end)
    }

    override fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: Int,

    ): Flow<List<TrueRecord>> {
        return recordDao.getUserCategoryRecordsOrderedByDateTime(categoryId)
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        accountId: Int,
    ): Flow<List<TrueRecord>> {
        return recordDao.getUserAccountRecordsOrderedByDateTime(accountId)
    }

    override fun getUserTotalAmountByType(recordType: RecordType): Flow<Double> {
        return recordDao.getUserTotalAmountByType(recordType)
    }
}