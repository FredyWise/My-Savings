package com.fredy.core.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatMonthDateYear(): String {
    return DateTimeFormatter.ofPattern("MMM dd, yyyy").format(
        this
    )
}

fun LocalDateTime.formatMonthYear(): String {
    return DateTimeFormatter.ofPattern("MMMM, yyyy").format(
        this
    )
}

fun LocalDateTime.formatMonthDate(): String {
    return DateTimeFormatter.ofPattern("MMM dd").format(
        this
    )
}

fun LocalDateTime.formatDay(): String {
    return DateTimeFormatter.ofPattern("EEEE").format(
        this
    )
}

fun LocalDateTime.formatMonthDateDay(): String {
    return "${formatMonthDate()}, ${formatDay()}"
}

fun LocalDateTime.formatMonthDateTime(): String {
    return "${formatMonthDate()}, ${formatTime()}"
}

fun LocalDateTime.formatDateDay(): String {
    return DateTimeFormatter.ofPattern("dd, EEEE").format(
        this
    )
}

fun LocalDateTime.formatTime(): String {
    return DateTimeFormatter.ofPattern("hh:mm a").format(
        this
    )
}

fun LocalDateTime.formatDetailedTime(): String {
    return DateTimeFormatter.ofPattern("hh:mm:ss.SSS a").format(
        this
    )
}

fun LocalDateTime.formatMonthDateYearDetailedTime(): String {
    return "${formatMonthDateYear()}${formatDetailedTime()}"
}

