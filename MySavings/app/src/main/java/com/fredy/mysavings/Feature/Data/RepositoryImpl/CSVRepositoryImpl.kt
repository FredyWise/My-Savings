package com.fredy.mysavings.Feature.Data.RepositoryImpl

import com.fredy.mysavings.Feature.Data.CSV.CSVDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Util.DefaultData
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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