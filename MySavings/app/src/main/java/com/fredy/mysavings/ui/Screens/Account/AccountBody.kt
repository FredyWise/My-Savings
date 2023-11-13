package com.fredy.mysavings.ui.Screens.Account

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.formatBalanceAmount
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Event.AccountEvent
import com.fredy.mysavings.ui.ActionWithName
import com.fredy.mysavings.ui.AdvancedEntityItem
import com.fredy.mysavings.ui.CustomStickyHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountBody(
    modifier: Modifier = Modifier,
    accounts: List<Account>,
    onEvent: (AccountEvent) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        stickyHeader {
            CustomStickyHeader(
                title = "Accounts",
                textStyle = MaterialTheme.typography.titleLarge
            )
        }
        items(accounts) { account ->
            AdvancedEntityItem(
                modifier = Modifier
                    .padding(
                        vertical = 4.dp
                    )
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .background(
                        MaterialTheme.colorScheme.surface
                    ),
                icon = account.accountIcon,
                iconDescription = account.accountIconDescription,
                iconModifier = Modifier
                    .size(65.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium
                    ),
                menuItems = listOf(
                    ActionWithName(name = "Delete Account",
                        action = {
                            onEvent(
                                AccountEvent.DeleteAccount(
                                    account
                                )
                            )
                        }),
                    ActionWithName(name = "Edit Account",
                        action = {
                            onEvent(
                                AccountEvent.ShowDialog(
                                    account
                                )
                            )
                        })
                )
            ) {
                Text(
                    text = account.accountName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "BALANCE" + ": " + formatBalanceAmount(
                        amount = account.accountAmount,
                        currency = account.accountCurrency
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
