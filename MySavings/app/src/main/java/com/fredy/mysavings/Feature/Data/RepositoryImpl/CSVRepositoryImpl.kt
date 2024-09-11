package com.fredy.mysavings.Feature.Data.RepositoryImpl

import com.fredy.data.CSV.CSVDao
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.CSVRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CSVRepositoryImpl(
    private val csvDao: CSVDao,
) : CSVRepository {

    override suspend fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String
    ) {
        withContext(Dispatchers.IO) {
            csvDao.outputToCSV(directory, filename, trueRecords, delimiter)
        }
    }

    override suspend fun inputFromCSV(
        currentUserId:String,
        directory: String,
        delimiter: String,
    ): List<TrueRecord> {
        return withContext(Dispatchers.IO) {
            val trueRecords = csvDao.inputFromCSV(directory, delimiter)
            Log.e("inputFromCSVRepo1: $trueRecords")
            trueRecords
        }
    }

}