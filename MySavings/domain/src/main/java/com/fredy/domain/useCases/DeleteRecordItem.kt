package com.fredy.domain.useCases

import com.fredy.domain.model.Record
import com.fredy.domain.repository.RecordRepository

class DeleteRecordItem(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(record: Record) {
        recordRepository.deleteRecordItem(record)
    }
}