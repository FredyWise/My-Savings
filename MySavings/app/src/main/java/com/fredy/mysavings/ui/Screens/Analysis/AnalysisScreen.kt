package com.fredy.mysavings.ui.Screens.Analysis


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.CurrencyViewModel
import com.fredy.mysavings.ui.Screens.CurrencyDropdown

@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrencyViewModel = hiltViewModel(),
) {
    var selectedText1 by remember {
        mutableStateOf("")
    }
    var selectedText2 by remember {
        mutableStateOf("")
    }
    Column {
        TextField(
            value = selectedText1,
            onValueChange = { selectedText1 = it })
        CurrencyDropdown(selectedText = selectedText2,
            onClick = { selectedText2 = it })
        Button(onClick = {
            viewModel.convert(
                selectedText1,
                selectedText2
            )
        }) {
            viewModel.resource.value.success?.let {
                Text(text = it)
            }
        }
    }

}

//val accounts = remember {
//        UserData.accounts.filter { account -> account.balance.amount >= 0 }.map { account -> account }
//    }
//    val amountsTotal = remember { accounts.map { account -> account.balance.amount }.sum() }
//    StatementBody(modifier = modifier,
//        items = accounts,
//        amounts = { account -> account.balance.amount },
//        amountsTotal = amountsTotal,
//        circleLabel = stringResource(R.string.total),
//        rows = { account ->
//            simpleButton(modifier = Modifier.padding(
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
//                        text = "Balance" + ": " + formatBalanceAmount(
//                            amount = account
//                        ),
//                        style = MaterialTheme.typography.titleMedium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                }
//            }
//        })