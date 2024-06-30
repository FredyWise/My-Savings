package com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel

import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.FilterType
import java.time.LocalDate

sealed interface RecordEvent {
    data class ShowDialog(val trueRecord: TrueRecord) : RecordEvent
    object HideDialog : RecordEvent
    object ShowFilterDialog : RecordEvent
    object HideFilterDialog : RecordEvent
    object ToggleRecordType : RecordEvent
    data class SelectedCurrencies(val selectedCurrencies: List<String>) : RecordEvent
    data class ChangeDate(val selectedDate: LocalDate) : RecordEvent
    data class FilterRecord(val filterType: FilterType) : RecordEvent
    object ShowNextList : RecordEvent
    object ShowPreviousList : RecordEvent
    object ToggleSortType : RecordEvent
    object ToggleShowTotal : RecordEvent
    object ToggleUserCurrency : RecordEvent
    object ToggleCarryOn : RecordEvent
    data class DeleteRecord(val record: Record) : RecordEvent
    object UpdateRecord : RecordEvent
    data class ClickBook(val book: Book) : RecordEvent

}