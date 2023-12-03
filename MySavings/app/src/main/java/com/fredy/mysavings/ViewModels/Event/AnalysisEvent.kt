package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Database.Enum.FilterType
import com.fredy.mysavings.Repository.TrueRecord

sealed interface AnalysisEvent{
    object ShowFilterDialog: AnalysisEvent
    object HideFilterDialog: AnalysisEvent
    object ShowNextList: AnalysisEvent
    object ShowPreviousList: AnalysisEvent
    data class FilterRecord(val filterType: FilterType): AnalysisEvent
    data class DeleteRecord(val record: Record): AnalysisEvent

}