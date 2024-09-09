package com.fredy.domain.repository


import com.fredy.domain.model.TrueRecord

interface CSVRepository {
    suspend fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    )

    suspend fun inputFromCSV(
        currentUserId: String,
        directory: String,
        delimiter: String = ",",
    ): List<TrueRecord>
}

