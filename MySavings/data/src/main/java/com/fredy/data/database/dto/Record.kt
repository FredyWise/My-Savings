package com.fredy.data.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.data.database.converter.TimestampConverter
import com.google.firebase.Timestamp
import java.time.LocalDateTime

@Entity
data class Record(
    @PrimaryKey
    val recordId: String = "",
    val walletIdFromFk: String = "",
    val walletIdToFk: String = "",
    val categoryIdFk: String = "",
    val userIdFk: String = "",
    val bookIdFk: String = "",
    val recordTimestamp: Timestamp = Timestamp.now(),
    val recordAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordType: com.fredy.domain.enums.RecordType = com.fredy.domain.enums.RecordType.Expense,
    val recordNotes: String = "",
) {
    val recordDateTime: LocalDateTime
        get() = TimestampConverter.toDateTime(recordTimestamp)
    //        set(value) { TimestampConverter.fromDateTime(value) }

    constructor(
        recordId: String,
        accountIdFromFk: String,
        accountIdToFk: String,
        categoryIdFk: String,
        bookId: String,
        recordDateTime: LocalDateTime,
        recordAmount: Double,
        recordCurrency: String,
        recordType: com.fredy.domain.enums.RecordType,
        recordNotes: String,
    ) : this(
        recordId,
        accountIdFromFk,
        accountIdToFk,
        categoryIdFk,
        "",
        bookId,
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
