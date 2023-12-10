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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ui.Screens.AdvancedEntityItem
import com.fredy.mysavings.ui.Screens.CustomStickyHeader

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
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.background
                ),
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
                    .size(50.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium
                    ),
                menuItems = listOf(
                    ActionWithName(
                        name = "Delete Account",
                        action = {
                            onEvent(
                                AccountEvent.DeleteAccount(
                                    account
                                )
                            )
                        },
                    ), ActionWithName(
                        name = "Edit Account",
                        action = {
                            onEvent(
                                AccountEvent.ShowDialog(
                                    account
                                )
                            )
                        },
                    )
                )
            ) {
                Text(
                    text = account.accountName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Balance: " + formatBalanceAmount(
                        amount = account.accountAmount,
                        currency = account.accountCurrency
                    ),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = BalanceColor(amount = account.accountAmount)
                )
            }
        }
    }
}
