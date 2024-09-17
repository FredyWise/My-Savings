package com.fredy.theme.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.fredy.theme.util.BalanceColors


@Composable
fun BalanceColor(
    amount: Double,
    isTransfer: Boolean = false
): Color {
    val expenseColor by remember { mutableStateOf(BalanceColors.Expense) }
    val transferColor by remember { mutableStateOf(BalanceColors.Transfer) }
    val incomeColor by remember { mutableStateOf(BalanceColors.Income) }

    return when {
        amount < 0.0 -> expenseColor
        isTransfer -> transferColor
        else -> incomeColor
    }
}
