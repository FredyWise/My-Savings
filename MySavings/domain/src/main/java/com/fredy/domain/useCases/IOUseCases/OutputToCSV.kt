package com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases

import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.CSVRepository

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