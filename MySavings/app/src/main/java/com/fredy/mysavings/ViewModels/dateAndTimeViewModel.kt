package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class dateAndTimeViewModel: ViewModel() {
    private var pickedDate by mutableStateOf(LocalDate.now())
    private var pickedTime by mutableStateOf(LocalTime.now())
    val formattedDate: String by derivedStateOf {
        DateTimeFormatter
            .ofPattern("MMM dd, yyyy")
            .format(pickedDate)
    }
    val formattedTime: String by derivedStateOf {
        DateTimeFormatter
            .ofPattern("hh:mm")
            .format(pickedTime)
    }
    val dateDialogState = MaterialDialogState()
    val timeDialogState = MaterialDialogState()

    fun updateDate(date: LocalDate) {
        pickedDate = date
    }

    fun updateTime(time: LocalTime) {
        pickedTime = time
    }


}

