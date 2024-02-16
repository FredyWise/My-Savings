package com.fredy.mysavings.ui.Screens.Account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatTime
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.AccountState
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.Record.RecordDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DetailAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleButton
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
    recordState: RecordState,
    recordEvent: (RecordsEvent) -> Unit,
) {
    recordState.trueRecord?.let {
        RecordDialog(
            trueRecord = it,
            onSaveClicked = { record ->
                recordEvent(
                    RecordsEvent.DeleteRecord(
                        record
                    )
                )
            },
            onDismissDialog = {
                recordEvent(
                    RecordsEvent.HideDialog
                )
            },
            onEdit = {
                rootNavController.navigate(
                    NavigationRoute.Add.route + "/" + it.record.recordId
                )
            },
        )
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.padding(top = 24.dp),
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                isSheetOpen = false
            },
            dragHandle = {},
        ) {
            DetailAppBar(
                title = "Account details",
                resource = state.recordMapsResource,
                icon = state.account.accountIcon,
                iconDescription = state.account.accountIconDescription,
                itemName = state.account.accountName,
                itemInfo = formatBalanceAmount(
                    state.account.accountAmount,
                    state.accountCurrency,
                ),
                onNavigationIconClick = {
                    isSheetOpen = false
                },
            ) { item, onBackgroundColor ->
                SimpleEntityItem(
                    modifier = Modifier.padding(
                        vertical = 4.dp
                    ),
                    iconModifier = Modifier
                        .size(
                            40.dp
                        )
                        .clip(
                            shape = CircleShape
                        ),
                    icon = item.toCategory.categoryIcon,
                    iconDescription = item.toCategory.categoryIconDescription,
                    endContent = {
                        Text(
                            text = formatTime(
                                item.record.recordDateTime.toLocalTime()
                            ),
                            color = onBackgroundColor,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                ) {
                    Text(
                        text = if (isTransfer(item.record.recordType)) {
                            item.fromAccount.accountName + " -> " + item.toAccount.accountName
                        } else item.toCategory.categoryName,
                        color = onBackgroundColor,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                    Text(
                        text = formatBalanceAmount(
                            item.record.recordAmount,
                            item.record.recordCurrency,
                            true
                        ),
                        color = onBackgroundColor,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                }

            }
        }
    }
    Column(modifier = modifier) {
        if (state.isAddingAccount) {
            AccountAddDialog(
                state = state, onEvent = onEvent, onSaveEffect = {
                    recordEvent(RecordsEvent.UpdateRecord)
                }
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
        SearchBar(
            searchText = state.searchQuery,
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
            },
        )
        SimpleButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 50.dp
                )
                .padding(top = 16.dp)
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

        state.accountResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "You Didn't Have Any Account Yet",
                isNullOrEmpty = { it.isNullOrEmpty() },
                errorMessage = resource.message ?: "",
                onMessageClick = {
                    onEvent(
                        AccountEvent.ShowDialog(
                            Account(accountName = "")
                        )
                    )
                },
            ) { data ->
                AccountBody(
                    accounts = data,
                    onEvent = onEvent,
                    onEntityClick = {
                        isSheetOpen = true
                    },
                    onDeleteAccount = {
                        recordEvent(RecordsEvent.UpdateRecord)
                    }
                )
            }
        }

    }
}

