package com.fredy.mysavings.Feature.Domain.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Feature.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Presentation.Util.formatDateDay
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
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
    val recordType: RecordType = RecordType.Expense,
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
        recordType: RecordType,
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


    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "${formatDateDay(recordDateTime)}",
            "$recordAmount",
            "${recordType.name}",
            "$recordCurrency",
            "$recordNotes",
        )

        val queries = query.split(",", " ")

        return queries.any { singleQuery ->
            matchingCombinations.any {
                it.contains(singleQuery.trim(), ignoreCase = true)
            }
        }
    }
}

//data class RecordsData(
//    val csvName: String = LocalDateTime.now().toString().replace(
//        "-", "_"
//    ).replace(":", ""),
//    var records: List<Record>  //this should be taken from csv file
//)
// this is imposible
