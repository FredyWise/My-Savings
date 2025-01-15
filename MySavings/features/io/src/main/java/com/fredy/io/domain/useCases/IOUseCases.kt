package com.fredy.io.domain.useCases



data class IOUseCases(
    val outputToCSV: OutputToCSV,
    val inputFromCSV: InputFromCSV,
    val upsertTrueRecords: UpsertTrueRecords,
    val getAllTrueRecordsWithinSelectedBook: GetAllTrueRecordsWithinSelectedBook,
    val getDBInfo: GetDBInfo,
    val getUserBooks: GetUserBooks
)


