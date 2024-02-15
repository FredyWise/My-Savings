package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.FilterType
import java.time.LocalDate

sealed interface RecordsEvent{
    data class ShowDialog(val trueRecord: TrueRecord): RecordsEvent
    object HideDialog: RecordsEvent
    object ShowFilterDialog: RecordsEvent
    object HideFilterDialog: RecordsEvent
    object ToggleRecordType: RecordsEvent
    data class SelectedCurrencies(val selectedCurrencies: List<String>): RecordsEvent
    data class ChangeDate(val selectedDate: LocalDate): RecordsEvent
    data class FilterRecord(val filterType: FilterType): RecordsEvent
    object ShowNextList: RecordsEvent
    object ShowPreviousList: RecordsEvent
    object ToggleSortType: RecordsEvent
    object ToggleShowTotal: RecordsEvent
    object ToggleCarryOn: RecordsEvent
    data class DeleteRecord(val record: Record): RecordsEvent

}