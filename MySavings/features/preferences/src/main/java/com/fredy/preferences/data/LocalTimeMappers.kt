package com.fredy.preferences.data

import java.time.LocalTime



fun LocalTime.toInt(): Int {
    return this.hour * 100 + this.minute
}

fun Int.toLocalTime(): LocalTime {
    val hour = this / 100
    val minute = this % 100
    val result = if (this != -1) LocalTime.of(hour, minute) else LocalTime.now()
    return result
}