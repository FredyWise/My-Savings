package com.fredy.mysavings.Data.RoomDatabase.Event

import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.FilterType
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import java.time.LocalDateTime

sealed interface RecordsEvent{
    data class ShowDialog(val trueRecord: TrueRecord): RecordsEvent
    object HideDialog: RecordsEvent
    data class SortRecord(val filterType: FilterType): RecordsEvent
    data class DeleteRecord(val record: Record): RecordsEvent

}