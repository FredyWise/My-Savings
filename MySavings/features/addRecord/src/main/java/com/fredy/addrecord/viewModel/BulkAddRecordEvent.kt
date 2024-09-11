package com.fredy.addrecord.viewModel

import android.net.Uri
import com.fredy.domain.model.Record

sealed interface BulkAddRecordEvent:AddRecordEvent {
    data class ImageToRecords (val imageUri: Uri): BulkAddRecordEvent
    data class UpdateRecord(val record: Record): BulkAddRecordEvent
    data class DeleteRecord(val record: Record): BulkAddRecordEvent
    object CloseAddRecordItemDialog: BulkAddRecordEvent
    data class ShowAddRecordItemDialog(val record: Record): BulkAddRecordEvent
}