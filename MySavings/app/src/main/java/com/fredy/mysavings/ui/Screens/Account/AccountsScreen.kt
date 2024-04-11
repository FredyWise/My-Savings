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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModels.AccountState
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.Record.RecordDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
//    recordState: RecordState,
    recordEvent: (RecordsEvent) -> Unit,
) {
//    recordState.trueRecord?.let {
//        RecordDialog(
//            trueRecord = it,
//            onSaveClicked = { record ->
//                recordEvent(
//                    RecordsEvent.DeleteRecord(
//                        record
//                    )
//                )
//            },
//            onDismissDialog = {
//                recordEvent(
//                    RecordsEvent.HideDialog
//                )
//            },
//            onEdit = {
//                rootNavController.navigate(
//                    "${NavigationRoute.Add.route}?recordId=${it.record.recordId}"
//                )
//            },
//        )
//    }
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    AccountDetailBottomSheet(
        isSheetOpen = isSheetOpen,
        onCloseBottomSheet = { isSheetOpen = it },
        state = state,
        recordEvent = recordEvent
    )
    AccountAddDialog(
        state = state, onEvent = onEvent, onSaveEffect = {
            recordEvent(RecordsEvent.UpdateRecord)
        }
    )

    Column(modifier = modifier) {
        AccountHeader(
            modifier = Modifier
                .height(150.dp)
                .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large, clip = true)
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
                    topItem = {
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
                    },
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

