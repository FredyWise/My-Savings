package com.fredy.mysavings.Feature.Data.CSV

import com.fredy.mysavings.Feature.Domain.Model.TrueRecord


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

