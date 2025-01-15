package com.fredy.domain.model

import com.fredy.domain.enums.BudgetPeriodType


data class Budget(
    val budgetId: String = "",
    val userIdFk: String = "",
    val category: Category = Category(),
    var amount: Double = 0.0,
    val allocated: Double = 0.0, // needs to add allocated balance on wallet
    var spent: Double = 0.0,
    var period: BudgetPeriodType = BudgetPeriodType.MONTHLY
) {
    var dailyBudget: Double = 0.0
    var weeklyBudget: Double = 0.0
    var monthlyBudget: Double = 0.0
    var yearlyBudget: Double = amount

    init {
        calculateBudgets()
    }

    fun updateAmount(newAmount: Double) {
        amount = newAmount
        calculateBudgets()
    }

    fun updatePeriod(newPeriod: BudgetPeriodType) {
        period = newPeriod
        calculateBudgets()
    }

    private fun calculateBudgets() {
        yearlyBudget = when (period) {
            BudgetPeriodType.DAILY -> amount * 365
            BudgetPeriodType.WEEKLY -> amount * 52
            BudgetPeriodType.MONTHLY -> amount * 12
            BudgetPeriodType.YEARLY -> amount
        }

        dailyBudget = yearlyBudget / 365
        weeklyBudget = yearlyBudget / 52
        monthlyBudget = yearlyBudget / 12
    }
}


