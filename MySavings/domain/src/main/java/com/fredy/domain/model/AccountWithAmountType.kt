package com.fredy.domain.model


data class AccountWithAmountType(
    val wallet: Wallet = Wallet(),
    val expenseAmount: Double = 0.0,
    val incomeAmount: Double = 0.0
)