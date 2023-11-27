package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Database.Enum.RecordType
import java.time.LocalDate
import java.time.LocalTime

sealed interface AddRecordEvent {
    data class SaveRecord(val navigateUp: () -> Unit): AddRecordEvent
    data class AccountIdFromFk(val fromAccount: Account): AddRecordEvent
    data class AccountIdToFk(val toAccount: Account): AddRecordEvent
    data class CategoryIdFk(val toCategory: Category): AddRecordEvent
    data class RecordDate(val date: LocalDate): AddRecordEvent
    data class RecordTime(val time: LocalTime): AddRecordEvent
    data class RecordAmount(val amount: Double): AddRecordEvent
    data class RecordCurrency(val currency: String): AddRecordEvent
    data class RecordTypes(val recordType: RecordType): AddRecordEvent
    data class RecordNotes(val notes: String): AddRecordEvent
    data class SetId(val id: String): AddRecordEvent

}