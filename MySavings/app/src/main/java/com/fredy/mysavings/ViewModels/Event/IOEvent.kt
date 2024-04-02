package com.fredy.mysavings.ViewModels.Event

import androidx.compose.ui.graphics.Color
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.fredy.mysavings.Data.Enum.ChangeColorType
import com.fredy.mysavings.Data.Enum.DisplayState
import java.time.LocalDate
import java.time.LocalTime

sealed interface IOEvent {
    data class SelectStartExportDate(val startDate: LocalDate) : IOEvent
    data class SelectEndExportDate(val endDate: LocalDate) : IOEvent
    data class OnExport(val uri: String) : IOEvent
    data class OnImport(val uri: MPFile<Any>) : IOEvent
    object OnClickedExport: IOEvent
    object OnAfterClickedImport: IOEvent

}