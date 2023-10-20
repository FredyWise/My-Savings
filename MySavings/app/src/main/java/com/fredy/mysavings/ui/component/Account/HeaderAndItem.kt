package com.fredy.mysavings.ui.component.Account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.fredy.mysavings.Data.Balance
import com.fredy.mysavings.ui.component.BalanceItem
import com.fredy.mysavings.ui.component.BasicButton

@Composable
fun AccountHeader() {
    Column {
        Row {
            BalanceItem(title = "Expense So Far", balance = Balance())
            BalanceItem(title = "Expense So Far", balance = Balance())
        }
        BalanceItem(title = "Expense So Far", balance = Balance())
    }
}

