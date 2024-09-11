package com.fredy.io.domain.useCases

data class IOUseCases(
    val outputToCSV: OutputToCSV,
    val inputFromCSV: InputFromCSV,
    val upsertTrueRecords: UpsertTrueRecords,
    val getDBInfo: GetDBInfo
)


