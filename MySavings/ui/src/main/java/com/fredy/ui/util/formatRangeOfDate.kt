package com.fredy.ui.util


import com.fredy.domain.enums.FilterType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

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
