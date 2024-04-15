package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord

class FakeCSVRepository : CSVRepository {

    private val csvFile = mutableListOf<Pair<String,List<TrueRecord>>>()

    override suspend fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String
    ) {
        csvFile.add(Pair(directory+filename+delimiter,trueRecords))
    }

    override suspend fun inputFromCSV(
        currentUserId: String,
        directory: String,
        delimiter: String
    ): List<TrueRecord> {
        return csvFile.find { it.first.equals(directory+delimiter,ignoreCase = false) }!!.second
    }
}



