package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import java.time.LocalDateTime

@Entity
data class Record(
    @PrimaryKey(autoGenerate = false)
    var recordId: String = "",
    val accountIdFromFk: String = "",
    val accountIdToFk: String = "",
    val categoryIdFk: String = "",
    val userIdFk: String = "",
    val recordDateTime: LocalDateTime = LocalDateTime.now(),
    val recordAmount: Double = 0.0,
    val recordCurrency: String = "",
    val isTransfer: Boolean = false,
    val recordType: RecordType = RecordType.Expense,
    val recordNotes: String = "",
)

//data class RecordsData(
//    val csvName: String = LocalDateTime.now().toString().replace(
//        "-", "_"
//    ).replace(":", ""),
//    var records: List<Record>  //this should be taken from csv file
//)
// this is imposible
