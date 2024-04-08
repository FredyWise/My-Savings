package com.fredy.mysavings.Feature.Domain.Repository

import androidx.compose.runtime.mutableStateOf
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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



