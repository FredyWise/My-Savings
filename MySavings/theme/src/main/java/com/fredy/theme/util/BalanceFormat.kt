package com.fredy.theme.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.fredy.data.enums.RecordType
import java.text.DecimalFormat
import kotlin.math.abs

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



fun formatBalanceAmount(
    amount: Double,
    currency: String? = null,
    isShortenToChar: Boolean = false,
    k: Boolean = true,
    m: Boolean = true,
    b: Boolean = true
): String {
    val amountCurrency = if (currency != null) " $currency" else ""
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
