package com.fredy.mysavings.Repository

import androidx.room.Embedded
import androidx.room.Relation
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.SavingsDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject


interface RecordRepository {
    suspend fun upsertRecordItem(recordItem: Record)
    suspend fun deleteRecordItem(recordItem: Record)
    fun getRecordById(id: String): Flow<TrueRecord>
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
    fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        start: Int,
        end: Int
    ): Flow<Double>

    fun getUserTotalRecordBalance(): Flow<Double>
}

class RecordRepositoryImpl @Inject constructor(
    private val savingsDatabase: SavingsDatabase,
): RecordRepository {
    override suspend fun upsertRecordItem(
        recordItem: Record
    ) {
        Firebase.firestore.collection("record").add(
                recordItem
            ).addOnSuccessListener { documentReference ->
                val generatedId = documentReference.id
                recordItem.recordId = generatedId
            }
        savingsDatabase.recordDao.upsertRecordItem(
            recordItem
        )
    }

    override suspend fun deleteRecordItem(
        recordItem: Record
    ) {
        Firebase.firestore.collection("record").document(
                recordItem.recordId
            ).delete()
        savingsDatabase.recordDao.deleteRecordItem(
            recordItem
        )
    }

    override fun getRecordById(id: String): Flow<TrueRecord> {
        return savingsDatabase.recordDao.getRecordById(
            id
        )
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
