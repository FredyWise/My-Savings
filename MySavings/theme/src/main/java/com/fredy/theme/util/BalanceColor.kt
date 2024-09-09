package com.fredy.theme.util

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.fredy.data.enums.RecordType

object BalanceColor {
    var Expense = defaultDarkExpenseColor
    var Transfer = defaultDarkTransferColor
    var Income = defaultDarkIncomeColor
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

@Composable
fun RecordTypeColor(
    recordType: RecordType
): Color {
    val expenseColor by remember { mutableStateOf(BalanceColor.Expense) }
    val transferColor by remember { mutableStateOf(BalanceColor.Transfer) }
    val incomeColor by remember { mutableStateOf(BalanceColor.Income) }

    return when (recordType){
        RecordType.Expense -> expenseColor
        RecordType.Income -> incomeColor
        RecordType.Transfer -> transferColor
    }
}