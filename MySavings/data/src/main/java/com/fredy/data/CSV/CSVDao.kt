package com.fredy.data.CSV

import com.fredy.domain.model.TrueRecord


interface CSVDao {
    fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    )

    fun inputFromCSV(
        directory: String,
        delimiter: String = ","
    ): List<TrueRecord>
}

