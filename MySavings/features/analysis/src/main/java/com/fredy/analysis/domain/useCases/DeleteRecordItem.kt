package com.fredy.analysis.domain.useCases

import com.fredy.analysis.domain.AnalysisRepository
import com.fredy.domain.model.Record


class DeleteRecordItem(
    private val analysisRepository: AnalysisRepository
) {
    suspend operator fun invoke(record: Record) {
        analysisRepository.deleteRecordItem(record)
    }
}