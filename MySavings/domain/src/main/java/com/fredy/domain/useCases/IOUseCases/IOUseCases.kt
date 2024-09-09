package com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases

data class IOUseCases(
    val outputToCSV: OutputToCSV,
    val inputFromCSV: InputFromCSV,
    val upsertTrueRecords: UpsertTrueRecords,
    val getDBInfo: GetDBInfo
)


