package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import java.time.LocalDateTime

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true)
    val recordId: Int = 0,
    val accountIdFromFk: Int = -1,
    val accountIdToFk: Int = -1,
    val categoryIdFk: Int = -1,
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
