package com.fredy.book.domain


import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Record
import com.fredy.domain.model.TrueRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


interface RecordSpecificRepository {
    
    suspend fun deleteRecordItem(record: Record)
    
    fun getUserTrueRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Flow<List<TrueRecord>>
    

    fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: List<RecordType>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Record>>
    

}


