package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.formatBalanceAmount

@Composable
fun BalanceBar(
    modifier: Modifier = Modifier,
    amountBars: List<BalanceItem>,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        amountBars.forEach { type ->
            BalanceItem(
                modifier = Modifier
                    .padding(0.dp)
                    .weight(
                        1f
                    ),
                title = type.name,
                amount = type.amount,
                currency = type.currency,
            )
        }
    }
}

@Composable
fun BalanceItem(
    modifier: Modifier = Modifier,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    amount: Double,
    amountColor: Color = BalanceColor(
        amount = amount,
    ),
    currency: String,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    titleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    amountStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
    ) {
        Text(
            text = title,
            color = titleColor,
            style = titleStyle
        )
        Text(
            text = formatBalanceAmount(
                amount = amount,
                currency = currency
            ),
            color = amountColor,
            style = amountStyle
        )
    }
}
