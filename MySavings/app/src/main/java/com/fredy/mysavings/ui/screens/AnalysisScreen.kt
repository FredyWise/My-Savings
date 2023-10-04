package com.fredy.mysavings.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.FormatBalanceAmount
import com.fredy.mysavings.Data.User.AccountIcons
import com.fredy.mysavings.Data.User.UserData
import com.fredy.mysavings.R
import com.fredy.mysavings.ui.component.Analysis.StatementBody
import com.fredy.mysavings.ui.component.BasicButton

@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
) {
    val amountsTotal = remember { UserData.accounts.map { account -> account.balance.amount }.sum() }
    StatementBody(modifier = modifier,
        items = UserData.accounts,
        amounts = { account -> account.balance.amount },
        amountsTotal = amountsTotal,
        circleLabel = stringResource(R.string.total),
        rows = { account ->
            BasicButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = AccountIcons(account.icon),
                    contentDescription = account.iconDescription,
                    tint = account.iconColor,
                    modifier = Modifier.size(
                        width = 40.dp,
                        height = 40.dp
                    )
                )
                Column {
                    Text(text = account.name)
                    Text(
                        text = stringResource(id = R.string.balance_capitalized) + ": " + FormatBalanceAmount(
                            balance = account.balance
                        )
                    )
                }
            }
        })
}