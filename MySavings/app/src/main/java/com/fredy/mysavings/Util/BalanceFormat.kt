package com.fredy.mysavings.Util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Enum.RecordType
import java.text.DecimalFormat
import kotlin.math.absoluteValue


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
    return when {
        amount < 0.0 -> MaterialTheme.colorScheme.primary
        isTransfer -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.tertiary
    }
}

fun formatBalanceAmount(
    amount: Double,
    currency: String? = null,
    isShortenToChar: Boolean = false,
): String {
    val amountCurrency = if (currency.isNotNull()) " $currency" else ""
    return if (isShortenToChar) formatCharAmount(amount) + amountCurrency else formatAmount(amount) + amountCurrency
}

fun formatAmount(amount: Double): String {
    return amountDecimalFormat.format(amount)
}


private val amountDecimalFormat = DecimalFormat("#,##0.00")

fun formatCharAmount(amount: Double): String {
    return if (amount.absoluteValue < 1000) {
        String.format("%.2f", amount)
    } else if (amount.absoluteValue / 1000 >= 1 && amount.absoluteValue < 1_000_000) {
        String.format("%.2fK", amount / 1000)
    } else if (amount.absoluteValue / 1_000_000 >= 1 && amount.absoluteValue < 1_000_000_000) {
        String.format("%.2fM", amount / 1_000_000)
    } else {
        String.format(
            "%.2fT",
            amount / 1_000_000_000
        )
    }
}

