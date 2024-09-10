package com.fredy.domain.enumsChecker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.fredy.domain.enums.RecordType
import com.fredy.theme.util.BalanceColor

fun RecordType.isTransfer(): Boolean {
    return this == RecordType.Transfer
}

fun RecordType.isExpense(): Boolean {
    return this == RecordType.Expense
}

fun RecordType.isIncome(): Boolean {
    return this == RecordType.Income
}

@Composable
fun RecordType.recordTypeColor(): Color {
    val expenseColor by remember { mutableStateOf(BalanceColor.Expense) }
    val transferColor by remember { mutableStateOf(BalanceColor.Transfer) }
    val incomeColor by remember { mutableStateOf(BalanceColor.Income) }

    return when (this) {
        RecordType.Expense -> expenseColor
        RecordType.Income -> incomeColor
        RecordType.Transfer -> transferColor
    }
}