package com.fredy.ui.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun formatMonthDateYear(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "MMM dd, YYYY "
    ).format(date)
}

fun formatMonthYear(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "MMMM, YYYY "
    ).format(date)
}

fun formatMonthDate(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "MMM dd"
    ).format(date)
}

fun formatDay(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "EEEE"
    ).format(date)
}

fun formatDateDay(date: LocalDate): String {
    return formatMonthDate(date) + ", " + formatDay(date)
}

fun formatMonthDateTime(dateTime: LocalDateTime): String {
    return formatMonthDate(dateTime.toLocalDate()) + ", " + formatTime(dateTime.toLocalTime())
}

fun formatDateDay(dateTime: LocalDateTime): String {
    return DateTimeFormatter.ofPattern(
        "dd, EEEE"
    ).format(dateTime)
}

fun formatTime(time: LocalTime): String {
    return DateTimeFormatter.ofPattern(
        "hh:mm a"
    ).format(time)
}

fun formatDetailedTime(time: LocalTime): String {
    return DateTimeFormatter.ofPattern(
        "hh:mm:ss.SSS a"
    ).format(time)
}

fun formatMonthDateYearDetailedTime(dateTime: LocalDateTime): String {
    return formatMonthDateYear(dateTime.toLocalDate()) + formatDetailedTime(
        dateTime.toLocalTime()
    )
}




