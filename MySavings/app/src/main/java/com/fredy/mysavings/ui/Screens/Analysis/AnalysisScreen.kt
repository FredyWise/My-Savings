package com.fredy.mysavings.ui.Screens.Analysis
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.material.Icon
//import androidx.compose.material.Text
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import com.fredy.mysavings.Data.User.UserData
//import com.fredy.mysavings.Data.formatBalanceAmount
//import com.fredy.mysavings.R
//import com.fredy.mysavings.ui.Analysis.StatementBody
//import com.fredy.mysavings.ui.component.BasicButton
//
//@Composable
//fun AnalysisScreen(
//    modifier: Modifier = Modifier,
//) {
//    val accounts = remember {
//        UserData.accounts.filter { account -> account.balance.amount >= 0 }.map { account -> account }
//    }
//    val amountsTotal = remember { accounts.map { account -> account.balance.amount }.sum() }
//    StatementBody(modifier = modifier,
//        items = accounts,
//        amounts = { account -> account.balance.amount },
//        amountsTotal = amountsTotal,
//        circleLabel = stringResource(R.string.total),
//        rows = { account ->
//            BasicButton(modifier = Modifier.padding(
//                horizontal = 10.dp
//            ),
//                buttonBackgroundColor = MaterialTheme.colorScheme.surface,
//                onClick = { /*TODO*/ }) {
//                Icon(
//                    painter = painterResource(
//                        account.icon
//                    ),
//                    contentDescription = account.iconDescription,
//                    tint = account.iconColor,
//                    modifier = Modifier.width(
//                        width = 60.dp,
//                    )
//                )
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.Start,
//
//                    ) {
//                    Text(
//                        text = account.name,
//                        style = MaterialTheme.typography.titleMedium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                    Text(
//                        text = stringResource(id = R.string.balance_capitalized) + ": " + formatBalanceAmount(
//                            balance = account.balance
//                        ),
//                        style = MaterialTheme.typography.titleMedium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                }
//            }
//        })
//}