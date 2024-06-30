package com.fredy.mysavings.Feature.Domain.Repository


import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord

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

