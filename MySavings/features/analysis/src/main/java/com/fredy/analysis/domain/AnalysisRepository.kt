package com.fredy.analysis.domain


import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Record
import com.fredy.domain.model.RecordMap
import com.fredy.domain.model.TrueRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


interface AnalysisRepository {

    suspend fun deleteRecordItem(record: Record)

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


}


