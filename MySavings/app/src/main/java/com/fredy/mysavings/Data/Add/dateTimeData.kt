package com.fredy.mysavings.Data.Add

import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

data class dateTimeData (
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now()
)