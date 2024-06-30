package com.fredy.mysavings.Feature.Presentation.ViewModels.IOViewModel

import com.darkrockstudios.libraries.mpfilepicker.MPFile
import java.time.LocalDate

sealed interface IOEvent {
    data class SelectStartExportDate(val startDate: LocalDate) : IOEvent
    data class SelectEndExportDate(val endDate: LocalDate) : IOEvent
    data class OnExport(val uri: String) : IOEvent
    data class OnImport(val uri: MPFile<Any>) : IOEvent
    object OnClickedExport: IOEvent
    object OnAfterClickedImport: IOEvent
    data class OnChooseBook(val bookName: String): IOEvent

}