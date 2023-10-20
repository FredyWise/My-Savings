package com.fredy.mysavings.Data.User

import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Data.Balance
import com.fredy.mysavings.R

interface Type{
    var name: String
    var balance: Balance
    var icon: Int
    var iconDescription: String
    var iconColor: Color
}
data class Account(
    override var name: String = "Account",
    override var balance: Balance = Balance(),
    override var icon: Int = R.drawable.ic_bank_card,
    override var iconDescription: String = "",
    override var iconColor: Color = Color.Unspecified,
): Type

val accountIcons = listOf(
    R.drawable.ic_exchange,
    R.drawable.ic_mastercard,
    R.drawable.ic_visa,
)

data class Category(
    override var name: String = "Category",
    override var balance: Balance = Balance(),
    override var icon: Int = R.drawable.ic_tag,
    override var iconDescription: String = "",
    override var iconColor: Color = Color.Unspecified,
): Type

val categoryExpenseIcons = listOf(
    R.drawable.ic_mastercard,
    R.drawable.ic_visa,
)

val categoryIncomeIcons = listOf(
    R.drawable.ic_mastercard,
    R.drawable.ic_visa,
)