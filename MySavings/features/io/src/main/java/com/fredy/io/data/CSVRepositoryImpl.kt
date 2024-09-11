package com.fredy.io.data

import com.fredy.data.CSV.CSVDao
import com.fredy.domain.model.TrueRecord
import com.fredy.io.domain.CSVRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
        currentUserId: String,
        directory: String,
        delimiter: String,
    ): List<TrueRecord> {
        return withContext(Dispatchers.IO) {
            val trueRecords = csvDao.inputFromCSV(directory, delimiter)
            Timber.e("inputFromCSVRepo1: $trueRecords")
            trueRecords
        }
    }

}