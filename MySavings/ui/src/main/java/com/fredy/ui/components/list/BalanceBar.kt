package com.fredy.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fredy.domain.modelUI.BalanceItem

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

