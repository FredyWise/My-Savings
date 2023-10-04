package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.fredy.mysavings.Data.Add.dateTimeData
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class dateAndTimeViewModel: ViewModel() {
    var pickedDateTime by mutableStateOf(dateTimeData())
    val formattedDate: String by derivedStateOf {
        DateTimeFormatter
            .ofPattern("MMM dd, yyyy")
            .format(pickedDateTime.date)
    }
    val formattedTime: String by derivedStateOf {
        DateTimeFormatter
            .ofPattern("hh : mm")
            .format(pickedDateTime.time)
    }
    val dateDialogState = MaterialDialogState()
    val timeDialogState = MaterialDialogState()

    fun updateDate(date: LocalDate) {
        pickedDateTime = pickedDateTime.copy(date = date)
    }

    fun updateTime(time: LocalTime) {
        pickedDateTime = pickedDateTime.copy(time = time)
    }


}

