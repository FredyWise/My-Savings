package com.fredy.mysavings.Util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Data.Database.Enum.RecordType
import java.text.DecimalFormat


data class BalanceItem(
    var name: String = "",
    var amount: Double = 0.0,
    var currency: String = ""
)

fun isTransfer(recordType: RecordType):Boolean{
    return recordType == RecordType.Transfer
}

fun isExpense(recordType: RecordType):Boolean{
    return recordType == RecordType.Expense
}

fun isIncome(recordType: RecordType):Boolean{
    return recordType == RecordType.Income
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



fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}