package com.fredy.mysavings.Feature.Domain.Model

import com.fredy.domain.model.Wallet

data class AccountWithAmountType(
    val wallet: Wallet = Wallet(),
    val expenseAmount: Double = 0.0,
    val incomeAmount: Double = 0.0
)