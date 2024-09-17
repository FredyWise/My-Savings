package com.fredy.ui.util

import java.text.DecimalFormat
import kotlin.math.abs

fun formatBalanceAmount(
    amount: Double,
    currency: String? = null,
    isShortenToChar: Boolean = false,
    k: Boolean = true,
    m: Boolean = true,
    b: Boolean = true
): String {
    val amountCurrency = if (currency != null) " $currency" else ""
    val shortenChar = formatCharAmount(amount, k, m, b)
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
