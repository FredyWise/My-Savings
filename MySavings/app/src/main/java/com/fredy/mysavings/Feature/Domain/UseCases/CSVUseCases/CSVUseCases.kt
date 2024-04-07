package com.fredy.mysavings.Feature.Domain.UseCases.CSVUseCases

import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.ViewModels.DBInfo
import kotlinx.coroutines.flow.Flow

data class CSVUseCases(
    val outputToCSV: OutputToCSV,
    val inputFromCSV: InputFromCSV,
    val getDBInfo: GetDBInfo
)

class OutputToCSV(
    private val csvRepository: CSVRepository
) {
    suspend operator fun invoke(
        directory: String, filename: String, trueRecords: List<TrueRecord>,
        delimiter: String = ","
    ) {
        csvRepository.outputToCSV(directory, filename, trueRecords, delimiter)
    }
}

class InputFromCSV(
    private val csvRepository: CSVRepository
) {
    suspend operator fun invoke(
        directory: String,
        delimiter: String = ","
    ): List<TrueRecord> {
        return csvRepository.inputFromCSV(directory, delimiter)
    }

}

class GetDBInfo(
    private val csvRepository: CSVRepository
) {
    suspend operator fun invoke(): Flow<DBInfo> {
        return csvRepository.getDBInfo()
    }
}
