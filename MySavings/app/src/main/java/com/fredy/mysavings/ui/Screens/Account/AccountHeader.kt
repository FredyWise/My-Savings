package com.fredy.mysavings.ui.Screens.Account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.ViewModel.AccountState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.BalanceItem

@Composable
fun AccountHeader(
    modifier: Modifier = Modifier,
    state: AccountState
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(
                    1f
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BalanceItem(
                modifier = Modifier.weight(1f),
                title = "Expense So Far",
                amount = state.totalExpense,
                currency = "",
                titleStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                amountStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
            )
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(
                        2.dp
                    ),
                color = MaterialTheme.colorScheme.secondary
            )
            BalanceItem(
                modifier = Modifier.weight(1f),
                title = "Income So Far",
                amount = state.totalIncome,
                currency = "",
                titleStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                amountStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    2.dp
                ),
            color = MaterialTheme.colorScheme.secondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    4.dp
                )
                .weight(
                    1f
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BalanceItem(
                modifier = Modifier.weight(1f),
                title = "Total Balance",
                amount = state.totalAll,
                currency = "",
                titleStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                amountStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
            )
        }
    }
}