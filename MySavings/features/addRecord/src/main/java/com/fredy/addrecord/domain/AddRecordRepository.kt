package com.fredy.addrecord.domain


import com.fredy.domain.model.Record
import com.fredy.domain.model.TrueRecord


interface AddRecordRepository {
    suspend fun upsertRecordItem(record: Record): String
    suspend fun upsertAllRecordItems(records: List<Record>)
    suspend fun getRecordById(recordId: String): TrueRecord

}


