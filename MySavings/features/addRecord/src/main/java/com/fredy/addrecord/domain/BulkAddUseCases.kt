package com.fredy.addrecord.domain

import com.fredy.addrecord.domain.usecases.singleAdd.GetRecordById
import com.fredy.addrecord.domain.usecases.singleAdd.UpsertRecordItem

data class BulkAddUseCases(
    val upsertRecordItem: UpsertRecordItem,
    val getRecordById: GetRecordById,
)