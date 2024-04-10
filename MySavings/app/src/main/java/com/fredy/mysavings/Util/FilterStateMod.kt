package com.fredy.mysavings.Util

import com.fredy.mysavings.Feature.Data.Database.Model.Book
import com.fredy.mysavings.Feature.Data.Enum.FilterType
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

data class FilterState(
    val recordType: RecordType = RecordType.Expense,
    val filterType: FilterType = FilterType.Monthly,
    val sortType: SortType = SortType.DESCENDING,
    val carryOn: Boolean = true,
    val showTotal: Boolean = true,
    val useUserCurrency: Boolean = false,
    val currentBook: Book? = null,
    val currencies: List<String> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val start: LocalDateTime = LocalDateTime.of(
        LocalDate.now().with(
            TemporalAdjusters.firstDayOfMonth()
        ), LocalTime.MIN
    ),
    val end: LocalDateTime = LocalDateTime.of(
        LocalDate.now().with(
            TemporalAdjusters.lastDayOfMonth()
        ), LocalTime.MAX
    ),
    val updating:Boolean = false
)

fun FilterState.isFilterTypeMonthBelow(): Boolean{
    return when(filterType){
        FilterType.Yearly -> false
        FilterType.Per6Months -> false
        FilterType.Per3Months -> false
        FilterType.Monthly -> true
        FilterType.Weekly -> true
        FilterType.Daily -> true
    }
}

fun <T> FilterState.map(target: (start: LocalDateTime, end: LocalDateTime, RecordType, SortType, currencies: List<String>, useUserCurrency: Boolean) -> T): T {
    return when (filterType) {
        FilterType.Daily -> target(
            start,
            end,
            recordType,
            sortType,
            currencies,
            useUserCurrency
        )

        FilterType.Weekly -> target(
            start,
            end,
            recordType,
            sortType,
            currencies,
            useUserCurrency
        )

        FilterType.Monthly -> target(
            start,
            end,
            recordType,
            sortType,
            currencies,
            useUserCurrency
        )

        FilterType.Per3Months -> target(
            start,
            end,
            recordType,
            sortType,
            currencies,
            useUserCurrency
        )

        FilterType.Per6Months -> target(
            start,
            end,
            recordType,
            sortType,
            currencies,
            useUserCurrency
        )

        FilterType.Yearly -> target(
            start,
            end,
            recordType,
            sortType,
            currencies,
            useUserCurrency
        )
    }
}

fun FilterState.updateType(filterType: FilterType):FilterState{
    val startDate: LocalDateTime
    val endDate: LocalDateTime

    when (filterType) {
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
            val monthsToAdd: Long = if (filterType == FilterType.Per3Months) 2 else 5
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

    return this.copy(
        filterType = filterType,
        start = startDate,
        end = endDate
    )
}

fun FilterState.updateDate(
    selectedDate: LocalDate,
):FilterState {
    val startDate: LocalDateTime
    val endDate: LocalDateTime

    when (filterType) {
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
            val monthsToAdd: Long = if (filterType == FilterType.Per3Months) 2 else 5
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

    return this.copy(
        selectedDate = selectedDate,
        start = startDate,
        end = endDate
    )

}

fun FilterState.minusDate(
):FilterState {
    val selectedDate = when (filterType) {
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
    return this.updateDate(selectedDate)
}

fun FilterState.plusDate():FilterState {
     val selectedDate = when (filterType) {
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
    return this.updateDate(selectedDate)
}