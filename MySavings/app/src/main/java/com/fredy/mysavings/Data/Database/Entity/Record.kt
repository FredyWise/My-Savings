package com.fredy.mysavings.Data.Database.Entity

import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Enum.RecordType
import com.google.firebase.Timestamp
import java.time.LocalDateTime


data class Record(
    var recordId: String = "",
    val accountIdFromFk: String = "",
    val accountIdToFk: String = "",
    val categoryIdFk: String = "",
    val userIdFk: String = "",
    val recordTimestamp: Timestamp = Timestamp.now(),
    val recordAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordType: RecordType = RecordType.Expense,
    val recordNotes: String = "",
){
    val recordDateTime: LocalDateTime
        get() = TimestampConverter.toDateTime(recordTimestamp)
    constructor(
        recordId: String,
        accountIdFromFk: String,
        accountIdToFk: String,
        categoryIdFk: String,
        recordDateTime: LocalDateTime,
        recordAmount: Double,
        recordCurrency: String,
        recordType: RecordType,
        recordNotes: String,
    ): this(
        recordId,
        accountIdFromFk,
        accountIdToFk,
        categoryIdFk,
        "",
        TimestampConverter.fromDateTime(recordDateTime),
        recordAmount,
        recordCurrency,
        recordType,
        recordNotes
    )
}

//data class RecordsData(
//    val csvName: String = LocalDateTime.now().toString().replace(
//        "-", "_"
//    ).replace(":", ""),
//    var records: List<Record>  //this should be taken from csv file
//)
// this is imposible
