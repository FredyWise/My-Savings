package com.fredy.mysavings.Feature.Presentation.Screens.Wallet

import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletState
import com.fredy.mysavings.Feature.Presentation.Screens.Record.BalanceItem

@Composable
fun WalletHeader(
    modifier: Modifier = Modifier,
    borderSize: Dp = 3.dp/2,
    state: WalletState
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .border(
                width = borderSize,
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.large
            )
            ,
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
            state.balanceBar.expense.let {
                BalanceItem(
                    modifier = Modifier.weight(1f),
                    title = it.name,
                    amount = it.amount,
                    currency = it.currency,
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
                    .fillMaxHeight()
                    .width(
                        borderSize
                    ),
                color = MaterialTheme.colorScheme.secondary
            )
            state.balanceBar.income.let {
                BalanceItem(
                    modifier = Modifier.weight(1f),
                    title = it.name,
                    amount = it.amount,
                    currency = it.currency,
                    titleStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    amountStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    borderSize
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
            state.balanceBar.balance.let {
                BalanceItem(
                    modifier = Modifier.weight(1f),
                    title = it.name,
                    amount = it.amount,
                    currency = it.currency,
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
}