package com.fredy.theme.model

data class BalanceBar(
    val expense: BalanceItem = BalanceItem(),
    val income: BalanceItem = BalanceItem(),
    val balance: BalanceItem = BalanceItem(),
    val transfer: BalanceItem = BalanceItem(),
)