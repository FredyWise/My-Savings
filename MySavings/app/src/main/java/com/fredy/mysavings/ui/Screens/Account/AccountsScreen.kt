package com.fredy.mysavings.ui.Screens.Account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModel.AccountState
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ui.Screens.SearchBar
import com.fredy.mysavings.ui.Screens.SimpleButton

@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
) {
    Column(modifier = modifier) {
        if (state.isAddingAccount) {
            AccountAddDialog(
                state = state, onEvent = onEvent
            )
        }
        AccountHeader(
            modifier = Modifier
                .height(150.dp)
                .padding(
                    vertical = 4.dp
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.large
                )
                .clip(MaterialTheme.shapes.large)
                .background(
                    MaterialTheme.colorScheme.surface
                ), state = state
        )
        SimpleButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 50.dp
                )
                .clip(
                    MaterialTheme.shapes.medium
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
                ),
            image = R.drawable.ic_add_foreground,
            imageColor = MaterialTheme.colorScheme.onBackground,
            onClick = {
                onEvent(
                    AccountEvent.ShowDialog(
                        Account(accountName = "")
                    )
                )
            },
            title = "ADD NEW ACCOUNT",
            titleStyle = MaterialTheme.typography.titleLarge.copy(
                MaterialTheme.colorScheme.onBackground
            )
        )
        SearchBar(
            searchText = state.searchText,
            onValueChange = {
                onEvent(
                    AccountEvent.SearchAccount(
                        it
                    )
                )
            },
            isSearching = state.isSearching
        ) {
            AccountBody(
                accounts = state.accounts,
                onEvent = onEvent
            )
        }
    }
}

