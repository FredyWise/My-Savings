package com.fredy.mysavings.Data.Records

import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Data.Account
import com.fredy.mysavings.Data.Category
import com.fredy.mysavings.Data.tempRecords
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class RecordsData(
    val csvName: String = LocalDateTime.now().toString().replace(
            "-", "_"
        ).replace(":", ""),
    var records: List<Record> = tempRecords//(listOf <Record>()) //this should be taken from csv file
)

data class Record(
    val date: LocalDate, var items: List<Item>
)

data class Item(
    var amount: Double,
    var currency: String,
    var time: LocalTime,
    var account: Account,
    var toAccount: Account?,
    var category: Category?,
    val transfer: Boolean = toAccount != null,
    val priceColor: Color = if (amount < 0) {
        Color.Red
    } else if (transfer) {
        Color.Blue
    } else {
        Color.Green
    },
    var notes: String,
)



