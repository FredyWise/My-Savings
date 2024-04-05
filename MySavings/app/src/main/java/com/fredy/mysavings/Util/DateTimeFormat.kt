package com.fredy.mysavings.Util

import com.fredy.mysavings.Feature.Data.Enum.FilterType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

fun formatDateYear(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "MMM dd, YYYY "
    ).format(date)
}

fun formatMonthYear(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "MMMM, YYYY "
    ).format(date)
}

fun formatDate(date: LocalDate):String{
    return DateTimeFormatter.ofPattern(
        "MMM dd"
    ).format(date)
}

fun formatDateDay(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "MMM dd, EEEE "
    ).format(date)
}

fun formatDateTime(dateTime: LocalDateTime): String {
    return DateTimeFormatter.ofPattern(
        "MMM dd, hh:mm a"
    ).format(dateTime)
}
fun formatDay(dateTime: LocalDateTime): String {
    return DateTimeFormatter.ofPattern(
        "dd, EEEE"
    ).format(dateTime)
}

fun formatTime(time: LocalTime): String {
    return DateTimeFormatter.ofPattern(
        "hh:mm a"
    ).format(time)
}

fun formatDateYearTime(dateTime: LocalDateTime): String {
    return formatDateYear(dateTime.toLocalDate()) + formatTime(
        dateTime.toLocalTime()
    )
}

fun formatRangeOfDate(
    localDate: LocalDate, filterType: FilterType
): String {
    when (filterType) {
        FilterType.Yearly -> return localDate.format(
            DateTimeFormatter.ofPattern("yyyy")
        )

        FilterType.Per6Months -> return localDate.format(
            DateTimeFormatter.ofPattern("MMM, yyyy")
        ) + " - " + localDate.plusMonths(5).format(
            DateTimeFormatter.ofPattern("MMM, yyyy")
        )

        FilterType.Per3Months -> return localDate.format(
            DateTimeFormatter.ofPattern("MMM, yyyy")
        ) + " - " + localDate.plusMonths(2).format(
            DateTimeFormatter.ofPattern("MMM, yyyy")
        )

        FilterType.Monthly -> return localDate.format(
            DateTimeFormatter.ofPattern("MMMM, yyyy")
        )

        FilterType.Weekly -> return localDate.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.MONDAY
            )
        ).format(
            DateTimeFormatter.ofPattern("MMM, dd")
        ) + " - " + localDate.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.MONDAY
            )
        ).plusDays(6).format(
            DateTimeFormatter.ofPattern("MMM, dd")
        )

        FilterType.Daily -> return localDate.format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy")
        )
    }
}



