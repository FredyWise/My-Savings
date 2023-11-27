package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Database.Enum.FilterType
import com.fredy.mysavings.Repository.TrueRecord

sealed interface RecordsEvent{
    data class ShowDialog(val trueRecord: TrueRecord): RecordsEvent
    object HideDialog: RecordsEvent
    object ShowFilterDialog: RecordsEvent
    object HideFilterDialog: RecordsEvent
    object ShowNextList: RecordsEvent
    object ShowPreviousList: RecordsEvent
    data class FilterRecord(val filterType: FilterType): RecordsEvent
    data class DeleteRecord(val record: Record): RecordsEvent

}