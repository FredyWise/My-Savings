package com.fredy.mysavings.Util

import com.fredy.mysavings.R

data class SavingsIcon(
    val image: Int,
    val description: String,
)

val accountIcons = listOf(
    SavingsIcon(R.drawable.ic_mastercard, "Master Card"),
    SavingsIcon(R.drawable.ic_visa, "Visa"),

)

val categoryIcons = listOf(
    SavingsIcon(R.drawable.ic_mastercard, "Master Card"),
    SavingsIcon(R.drawable.ic_visa, "Visa"),
)


val transferIcon = SavingsIcon(R.drawable.ic_exchange, "Transfer")
val categoryInitIcon = SavingsIcon(R.drawable.ic_category_foreground, "Category")
val accountInitIcon = SavingsIcon(R.drawable.ic_wallet_foreground, "Account")

val appIcon = SavingsIcon(R.drawable.ic_wallet_foreground, "Application Icon")