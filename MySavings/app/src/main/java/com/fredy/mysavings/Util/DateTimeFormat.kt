package com.fredy.mysavings.Util

import com.fredy.mysavings.Data.RoomDatabase.Enum.FilterType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

fun formatDate(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "MMM dd, YYYY "
    ).format(date)
}

fun formatDay(date: LocalDate): String {
    return DateTimeFormatter.ofPattern(
        "MMM dd, EEEE "
    ).format(date)
}

fun formatTime(time: LocalTime): String {
    return DateTimeFormatter.ofPattern(
        "hh:mm"
    ).format(time)
}

fun formatDateTime(dateTime: LocalDateTime): String {
    return formatDate(dateTime.toLocalDate()) + formatTime(
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
                DayOfWeek.SUNDAY
            )
        ).format(
            DateTimeFormatter.ofPattern("MMM, dd")
        )

        FilterType.Daily -> return localDate.format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy")
        )
    }
}