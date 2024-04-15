package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Feature.Data.Database.Model.Wallet
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import java.time.LocalDate
import java.time.LocalTime

sealed interface AddRecordEvent {
    data class SaveRecord(val sideEffect: () -> Unit): AddRecordEvent
    data class AccountIdFromFk(val fromWallet: Wallet): AddRecordEvent
    data class AccountIdToFk(val toWallet: Wallet): AddRecordEvent
    data class CategoryIdFk(val toCategory: Category): AddRecordEvent
    data class RecordDate(val date: LocalDate): AddRecordEvent
    data class RecordTime(val time: LocalTime): AddRecordEvent
    data class RecordAmount(val amount: Double): AddRecordEvent
    data class RecordCurrency(val currency: String): AddRecordEvent
    data class RecordTypes(val recordType: RecordType): AddRecordEvent
    data class RecordNotes(val notes: String): AddRecordEvent
    object DismissWarning: AddRecordEvent
    object ConvertCurrency: AddRecordEvent

}