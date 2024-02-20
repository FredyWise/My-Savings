package com.fredy.mysavings.Util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Enum.RecordType
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.absoluteValue

data class BalanceBar(
    val expense: BalanceItem = BalanceItem(),
    val income: BalanceItem = BalanceItem(),
    val balance: BalanceItem = BalanceItem(),
    val transfer: BalanceItem = BalanceItem(),
)

data class BalanceItem(
    var name: String = "",
    var amount: Double = 0.0,
    var currency: String = ""
)

fun isTransfer(recordType: RecordType): Boolean {
    return recordType == RecordType.Transfer
}

fun isExpense(recordType: RecordType): Boolean {
    return recordType == RecordType.Expense
}

fun isIncome(recordType: RecordType): Boolean {
    return recordType == RecordType.Income
}

@Composable
fun BalanceColor(
    amount: Double,
    isTransfer: Boolean = false
): Color {
    val expenseColor by remember { mutableStateOf(BalanceColor.Expense) }
    val transferColor by remember { mutableStateOf(BalanceColor.Transfer) }
    val incomeColor by remember { mutableStateOf(BalanceColor.Income) }

    return when {
        amount < 0.0 -> expenseColor
        isTransfer -> transferColor
        else -> incomeColor
    }
}

object BalanceColor {
    var Expense = defaultExpenseColor
    var Transfer = defaultTransferColor
    var Income = defaultIncomeColor
}

fun formatBalanceAmount(
    amount: Double,
    currency: String? = null,
    isShortenToChar: Boolean = false,
    k: Boolean = true,
    m: Boolean = true,
    b: Boolean = true
): String {
    val amountCurrency = if (currency.isNotNull()) " $currency" else ""
    val shortenChar = formatCharAmount(amount,k, m, b)
    return if (isShortenToChar) shortenChar + amountCurrency else formatAmount(amount) + amountCurrency
}

private fun formatAmount(amount: Double): String {
    return amountDecimalFormat.format(amount)
}


private val amountDecimalFormat = DecimalFormat("#,##0.00")


private fun formatCharAmount(
    amount: Double,
    k: Boolean = true,
    m: Boolean = true,
    b: Boolean = true
): String {
    val thresholds = listOf<Long>(1_000_000_000_000, 1_000_000_000, 1_000_000, 1000)
    val units = listOf("T", "B", "M", "K")

    for ((threshold, unit) in thresholds.zip(units)) {
        if (abs(amount) >= threshold && (k || unit != "K") && (m || unit != "M") && (b || unit != "B")) {
            return String.format("%.2f$unit", amount / threshold)
        }
    }

    return amountDecimalFormat.format(amount)
}
