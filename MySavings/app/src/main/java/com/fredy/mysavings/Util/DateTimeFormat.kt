package com.fredy.mysavings.Util

import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.ViewModels.FilterState
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

fun updateFilterState(
    event: FilterType, selectedDate: LocalDate, currentFilterState: FilterState
): FilterState {
    val startDate: LocalDateTime
    val endDate: LocalDateTime

    when (event) {
        FilterType.Daily -> {
            startDate = LocalDateTime.of(
                selectedDate, LocalTime.MIN
            )
            endDate = LocalDateTime.of(
                selectedDate, LocalTime.MAX
            )
        }

        FilterType.Weekly -> {
            val monday = selectedDate.with(
                TemporalAdjusters.previousOrSame(
                    DayOfWeek.MONDAY
                )
            )
            startDate = LocalDateTime.of(
                monday, LocalTime.MIN
            )
            endDate = LocalDateTime.of(
                monday.plusDays(
                    6
                ), LocalTime.MAX
            )
        }

        FilterType.Monthly -> {
            startDate = LocalDateTime.of(
                selectedDate.with(
                    TemporalAdjusters.firstDayOfMonth()
                ), LocalTime.MIN
            )
            endDate = LocalDateTime.of(
                selectedDate.with(
                    TemporalAdjusters.lastDayOfMonth()
                ), LocalTime.MAX
            )
        }

        FilterType.Per3Months, FilterType.Per6Months -> {
            val monthsToAdd: Long = if (event == FilterType.Per3Months) 2 else 5
            startDate = LocalDateTime.of(
                selectedDate.with(
                    TemporalAdjusters.firstDayOfMonth()
                ), LocalTime.MIN
            )
            endDate = LocalDateTime.of(
                selectedDate.plusMonths(
                    monthsToAdd
                ).with(TemporalAdjusters.lastDayOfMonth()),
                LocalTime.MAX
            )
        }

        FilterType.Yearly -> {
            startDate = LocalDateTime.of(
                selectedDate.with(
                    TemporalAdjusters.firstDayOfYear()
                ), LocalTime.MIN
            )
            endDate = LocalDateTime.of(
                selectedDate.with(
                    TemporalAdjusters.lastDayOfYear()
                ), LocalTime.MAX
            )
        }
    }

    return currentFilterState.copy(
        filterType = event,
        start = startDate,
        end = endDate
    )
}

fun minusFilterDate(
    filterType: FilterType,
    selectedDate: LocalDate
): LocalDate {
    return when (filterType) {
        FilterType.Daily -> selectedDate.minusDays(
            1
        )

        FilterType.Weekly -> selectedDate.minusWeeks(
            1
        )

        FilterType.Monthly -> selectedDate.minusMonths(
            1
        )

        FilterType.Per3Months -> selectedDate.minusMonths(
            3
        )

        FilterType.Per6Months -> selectedDate.minusMonths(
            6
        )

        FilterType.Yearly -> selectedDate.minusYears(
            1
        )
    }
}

fun plusFilterDate(
    filterType: FilterType,
    selectedDate: LocalDate
): LocalDate {
    return when (filterType) {
        FilterType.Daily -> selectedDate.plusDays(
            1
        )

        FilterType.Weekly -> selectedDate.plusWeeks(
            1
        )

        FilterType.Monthly -> selectedDate.plusMonths(
            1
        )

        FilterType.Per3Months -> selectedDate.plusMonths(
            3
        )

        FilterType.Per6Months -> selectedDate.plusMonths(
            6
        )

        FilterType.Yearly -> selectedDate.plusYears(
            1
        )
    }
}