package com.fredy.mysavings.Data.User

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.fredy.mysavings.Data.Balance
import com.fredy.mysavings.R

data class Account(
    var name: String = "Account",
    var balance: Balance = Balance(),
    var icon: AccountIcons = AccountIcons.DEFAULT,
    var iconDescription: String = "",
    var iconColor: Color = Color.Unspecified,
)

enum class AccountIcons {
    DEFAULT,
    MASTER_CARD,
    CREDIT_CARD,
    TRANSFER,
//    MONEY,
}

@Composable
fun AccountIcons(accountIcon: AccountIcons): Painter {
    return when (accountIcon) {
        AccountIcons.DEFAULT -> painterResource(
            id = R.drawable.ic_bank_card
        )
        AccountIcons.MASTER_CARD -> painterResource(
            id = R.drawable.ic_mastercard
        )
        AccountIcons.CREDIT_CARD -> painterResource(
            id = R.drawable.ic_visa
        )
        AccountIcons.TRANSFER -> painterResource(
            id = R.drawable.ic_exchange
        )
    }
}


