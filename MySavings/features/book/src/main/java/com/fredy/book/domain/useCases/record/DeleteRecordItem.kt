package com.fredy.book.domain.useCases.record

import com.fredy.book.domain.RecordSpecificRepository
import com.fredy.domain.model.Record


class DeleteRecordItem(
    private val recordSpecificRepository: RecordSpecificRepository
) {
    suspend operator fun invoke(record: Record) {
        recordSpecificRepository.deleteRecordItem(record)
    }
}