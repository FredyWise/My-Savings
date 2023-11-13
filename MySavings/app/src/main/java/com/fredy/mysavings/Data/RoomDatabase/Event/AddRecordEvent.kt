package com.fredy.mysavings.Data.RoomDatabase.Event

import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import java.time.LocalDate
import java.time.LocalTime

sealed interface AddRecordEvent{
    object SaveRecord: AddRecordEvent
    data class AccountIdFromFk(val fromAccount: Int): AddRecordEvent
    data class CategoryIdFk(val toCategory: Int): AddRecordEvent
    data class RecordDate(val date: LocalDate): AddRecordEvent
    data class RecordTime(val time: LocalTime): AddRecordEvent
    data class RecordAmount(val amount: Double): AddRecordEvent
    data class RecordCurrency(val currency: String): AddRecordEvent
    data class RecordTypes(val recordType: RecordType): AddRecordEvent
    data class RecordNotes(val notes: String): AddRecordEvent

}