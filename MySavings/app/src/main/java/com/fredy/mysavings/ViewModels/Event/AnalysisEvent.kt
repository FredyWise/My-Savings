package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Enum.FilterType
import java.time.LocalDate

sealed interface AnalysisEvent{
    object ShowFilterDialog: AnalysisEvent
    object HideFilterDialog: AnalysisEvent
    object ShowNextList: AnalysisEvent
    object ShowPreviousList: AnalysisEvent
    object ToggleRecordType: AnalysisEvent
    data class FilterRecord(val filterType: FilterType): AnalysisEvent
    data class DeleteRecord(val record: Record): AnalysisEvent
    data class ChangeDate(val selectedDate: LocalDate):AnalysisEvent

}