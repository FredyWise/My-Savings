package com.fredy.addrecord.domain.usecases.singleAdd


data class SingleAddUseCases(
    val upsertRecordItem: UpsertRecordItem,
    val getRecordById: GetRecordById,
)