package com.fredy.mysavings.Data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Data.User.UserData
import java.text.DecimalFormat

data class Balance(
    var amount: Float = 0f,
    var currency: String = "",
    var isTransfer: Boolean = false,
    val balanceColor: Color = if (amount < 0) {
        Color.Red
    } else if (isTransfer) {
        Color.Cyan
    } else {
        Color.Green
    },
)


@Composable
fun FormatBalanceAmount(balance: Balance): String{
//    if (balance.currency != UserData.currency){
//        balance.amount = balance.amount * ()
//    }
    return FormatAmount(balance.amount) + " " + UserData.currency
}

@Composable
fun FormatAmount(amount: Float): String{
    return amountDecimalFormat.format(amount)
}
private val amountDecimalFormat = DecimalFormat("#,###.##")



