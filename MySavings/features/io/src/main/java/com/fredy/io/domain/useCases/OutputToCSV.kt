package com.fredy.io.domain.useCases

import com.fredy.domain.model.TrueRecord
import com.fredy.io.domain.CSVRepository

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