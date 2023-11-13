package com.fredy.mysavings.Data.RoomDatabase.Event

import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import java.time.LocalDateTime

sealed interface RecordsEvent{
    object SaveRecord: RecordsEvent
    data class AccountIdFromFk(val fromAccount: Int): RecordsEvent
    data class CategoryIdFk(val toCategory: Int): RecordsEvent
    data class RecordDateTime(val dateTime: LocalDateTime): RecordsEvent
    data class RecordAmount(val amount: Double): RecordsEvent
    data class RecordCurrency(val currency: String): RecordsEvent
    data class RecordNotes(val notes: String): RecordsEvent
    data class ShowDialog(val trueRecord: TrueRecord): RecordsEvent
    object HideDialog: RecordsEvent
    data class SortRecord(val sortType: SortType): RecordsEvent
    data class DeleteRecord(val record: Record): RecordsEvent


    object Dummy: RecordsEvent
}