package com.fredy.mysavings.Data

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val balanceBars = listOf(
    BalanceBar.Income,
    BalanceBar.Expense,
    BalanceBar.Total
)

sealed class BalanceBar(
    var name: String = "",
    var amount: Double = 77777777.777,
    var currency: String = "USD"
) {
    object Income: BalanceBar(
        name = "INCOME",
    )

    object Expense: BalanceBar(
        name = "EXPENSE",
    )

    object Total: BalanceBar(
        name = "BALANCE",
    )
}

fun isTransfer(recordType: RecordType):Boolean{
    return recordType == RecordType.Transfer
}

fun isExpense(recordType: RecordType):Boolean{
    return recordType == RecordType.Expense
}
@Composable
fun BalanceColor(amount: Double, isTransfer: Boolean = false): Color {
    return when {
        amount < 0.0 -> MaterialTheme.colorScheme.secondary
        isTransfer -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
}

fun formatBalanceAmount(amount: Double, currency: String): String{
    return formatAmount(amount) + " " + currency
}

fun formatAmount(amount: Double): String{
    return amountDecimalFormat.format(amount)
}


private val amountDecimalFormat = DecimalFormat("#,##0.00")

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
        "hh : mm"
    ).format(time)
}

fun formatDateTime(dateTime: LocalDateTime): String {
    return formatDate(dateTime.toLocalDate()) + formatTime(dateTime.toLocalTime())
}

fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}