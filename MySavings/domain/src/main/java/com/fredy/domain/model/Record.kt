package com.fredy.domain.model

import com.fredy.domain.enums.RecordType
import com.fredy.theme.util.formatDateDay
import java.time.LocalDateTime


data class Record(
    val recordId: String = "",
    val walletIdFromFk: String = "",
    val walletIdToFk: String = "",
    val categoryIdFk: String = "",
    val userIdFk: String = "",
    val bookIdFk: String = "",
    val recordDateTime: LocalDateTime = LocalDateTime.now(),
    val recordAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordType: RecordType = RecordType.Expense,
    val recordNotes: String = "",
) {

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
