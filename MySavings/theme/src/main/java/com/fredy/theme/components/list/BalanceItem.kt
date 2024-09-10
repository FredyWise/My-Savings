package com.fredy.theme.components.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.fredy.theme.util.BalanceColor
import com.fredy.theme.util.formatBalanceAmount

@Composable
fun BalanceItem(
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
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
                currency = currency, isShortenToChar = true
            ),
            color = amountColor,
            style = amountStyle
        )
    }
}