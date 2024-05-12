package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel

import android.net.Uri
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Record
import java.time.LocalDate
import java.time.LocalTime

sealed interface AddRecordEvent {
    data class SaveRecord(val sideEffect: () -> Unit): AddRecordEvent
    data class AccountIdFromFk(val fromWallet: Wallet): AddRecordEvent
    data class AccountIdToFk(val toWallet: Wallet): AddRecordEvent
    data class CategoryIdFk(val toCategory: Category): AddRecordEvent
    data class RecordDate(val date: LocalDate): AddRecordEvent
    data class RecordTime(val time: LocalTime): AddRecordEvent
    object RecordAmount: AddRecordEvent
    data class RecordCurrency(val currency: String): AddRecordEvent
    data class RecordTypes(val recordType: RecordType): AddRecordEvent
    data class RecordNotes(val notes: String): AddRecordEvent
    object DismissWarning: AddRecordEvent
    object ConvertCurrency: AddRecordEvent

    //special Bulk
    data class ImageToRecords (val imageUri: Uri):AddRecordEvent
    data class UpdateRecord(val record: Record): AddRecordEvent
    data class DeleteRecord(val record: Record): AddRecordEvent
    object CloseAddRecordItemDialog: AddRecordEvent
    data class ShowAddRecordItemDialog(val record: Record): AddRecordEvent
}