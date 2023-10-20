package com.fredy.mysavings.ui.component.Records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fredy.mysavings.Data.Balance
import com.fredy.mysavings.Data.BalanceBar
import com.fredy.mysavings.Data.FormatBalanceAmount
import com.fredy.mysavings.ui.component.BalanceItem

@Composable
fun BalanceBar(
    modifier: Modifier = Modifier,
    balanceBars: List<BalanceBar>,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        balanceBars.forEach { type ->
            BalanceItem(
                modifier = Modifier.weight(1f),
                title = type.name,
                balance = type.balance
            )
        }
    }
}


