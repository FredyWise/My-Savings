package com.fredy.mysavings.Data

import com.fredy.mysavings.Data.BalanceBar.Expense.balance as ExpenseBalance
import com.fredy.mysavings.Data.BalanceBar.Income.balance as IncomeBalance

val balanceBars = listOf(
    BalanceBar.Income,
    BalanceBar.Expense,
    BalanceBar.Total
)

sealed class BalanceBar(
    var name: String = "",
    var balance: Balance = Balance()
) {
    object Income: BalanceBar(
        name = "INCOME",
    )

    object Expense: BalanceBar(
        name = "EXPENSE",
    )

    object Total: BalanceBar(
        name = "BALANCE",
        balance = Balance(amount = IncomeBalance.amount - ExpenseBalance.amount)
    )
}


