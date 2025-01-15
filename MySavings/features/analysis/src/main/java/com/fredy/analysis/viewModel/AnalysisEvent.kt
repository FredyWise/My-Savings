package com.fredy.analysis.viewModel

//import com.fredy.domain.model.Book
//import com.fredy.domain.model.Record
//import com.fredy.domain.model.TrueRecord
//import com.fredy.domain.enums.FilterType
//import java.time.LocalDate

//sealed interface AnalysisEvent {
//    data class ShowDialog(val trueRecord: TrueRecord) : AnalysisEvent
//    object HideDialog : AnalysisEvent
//    object ShowFilterDialog : AnalysisEvent
//    object HideFilterDialog : AnalysisEvent
//    object ToggleAnalysisType : AnalysisEvent
//    data class DeleteAnalysis(val record: Record) : AnalysisEvent
//    object UpdateAnalysis : AnalysisEvent
//    data class SelectedCurrencies(val selectedCurrencies: List<String>) : AnalysisEvent
//    data class ChangeDate(val selectedDate: LocalDate) : AnalysisEvent
//    data class FilterAnalysis(val filterType: FilterType) : AnalysisEvent
//    object ShowNextList : AnalysisEvent
//    object ShowPreviousList : AnalysisEvent
//    object ToggleSortType : AnalysisEvent
//    object ToggleShowTotal : AnalysisEvent
//    object ToggleUserCurrency : AnalysisEvent
//    object ToggleCarryOn : AnalysisEvent
//    data class ClickBook(val book: Book) : AnalysisEvent
//}