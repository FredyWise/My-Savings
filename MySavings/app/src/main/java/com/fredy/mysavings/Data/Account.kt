package com.fredy.mysavings.Data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.fredy.mysavings.R

data class Account(
    var name: String,
    var icon: AccountIcons,
    var iconDescription: String = "",
    var iconColor: Color = Color.Unspecified,
    var amount: Double = 0.0,
    var currency: String,
)

enum class AccountIcons {
    MASTER_CARD,
    CREDIT_CARD,
//    MONEY,
}

@Composable
fun AccountIcons(accountIcon: AccountIcons): Painter {
    return when (accountIcon) {
        AccountIcons.MASTER_CARD -> painterResource(
            id = R.drawable.ic_mastercard
        )

        AccountIcons.CREDIT_CARD -> painterResource(
            id = R.drawable.ic_visa
        )
//        AccountIcons.MONEY ->
    }
}


