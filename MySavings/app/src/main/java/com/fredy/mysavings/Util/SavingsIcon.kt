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

//val transferIcon = SavingsIcon(R.drawable.ic_exchange, "Transfer")