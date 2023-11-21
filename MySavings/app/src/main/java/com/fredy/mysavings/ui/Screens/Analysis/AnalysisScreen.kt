package com.fredy.mysavings.ui.Screens.Analysis


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
) {
    var expanded by remember {
        mutableStateOf(
            false
        )
    }
    val suggestions = listOf(
        "Kotlin", "Java", "Dart", "Python"
    )
    var filter by remember {
        mutableStateOf(suggestions)
    }
    var selectedText by remember {
        mutableStateOf(
            ""
        )
    }

    var textfieldSize by remember {
        mutableStateOf(
            Size.Zero
        )
    }

    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown


    Column(Modifier.padding(20.dp)) {
        OutlinedTextField(value = selectedText,
            onValueChange = {
                selectedText = it
                expanded = true
                filter = suggestions.filter { data ->
                    data.contains(it,ignoreCase = true)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text("Label") },
            trailingIcon = {
                Icon(icon,
                    "contentDescription",
                    Modifier.clickable { expanded = !expanded })
            })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(
                LocalDensity.current
            ) { textfieldSize.width.toDp() })
        ) {
            filter.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label
                    expanded = false
                }) {
                    Text(text = label)
                }
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