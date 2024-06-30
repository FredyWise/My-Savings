package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository

class DeleteRecordItem(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(record: Record) {
        recordRepository.deleteRecordItem(record)
    }
}