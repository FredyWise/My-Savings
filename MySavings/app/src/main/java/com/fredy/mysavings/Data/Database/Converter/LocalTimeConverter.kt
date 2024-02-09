package com.fredy.mysavings.Data.Database.Converter

import java.time.LocalTime

object LocalTimeConverter {
    fun localTimeToInt(time: LocalTime): Int {
        return time.hour * 100 + time.minute
    }

    fun intToLocalTime(value: Int): LocalTime {
        val hour = value / 100
        val minute = value % 100
        val result = if (value != -1) LocalTime.of(hour, minute) else LocalTime.now()
        return result
    }
}