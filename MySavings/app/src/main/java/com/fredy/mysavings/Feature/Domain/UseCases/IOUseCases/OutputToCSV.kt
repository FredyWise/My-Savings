package com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases

import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository

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