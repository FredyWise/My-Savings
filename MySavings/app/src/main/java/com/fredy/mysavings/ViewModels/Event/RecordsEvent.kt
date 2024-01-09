package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Data.Repository.TrueRecord
import java.time.LocalDate

sealed interface RecordsEvent{
    data class ShowDialog(val trueRecord: TrueRecord): RecordsEvent
    object HideDialog: RecordsEvent
    object ShowFilterDialog: RecordsEvent
    object HideFilterDialog: RecordsEvent
    data class SelectedCurrencies(val selectedCurrencies: List<String>): RecordsEvent
    data class ChangeDate(val selectedDate: LocalDate): RecordsEvent
    data class FilterRecord(val filterType: FilterType): RecordsEvent
    object ShowNextList: RecordsEvent
    object ShowPreviousList: RecordsEvent
    data class DeleteRecord(val record: Record): RecordsEvent

}