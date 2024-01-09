package com.fredy.mysavings.ui.Screens.Account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModels.AccountState
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ui.Screens.Category.CategoryDetailSheet
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
) {

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                isSheetOpen = false
            },
            dragHandle = {},
        ) {
            AccountDetailSheet(
                state = state,
                onBackIconClick = {
                    isSheetOpen = false
                },
            )
        }
    }
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
            title = "Add New Account",
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
            isSearching = state.isSearching,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        ) {
            AccountBody(
                accounts = state.accounts,
                onEvent = onEvent,
                onEntityClick = {
                    isSheetOpen = true
                }
            )
        }
    }
}

