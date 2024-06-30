package com.fredy.mysavings.Feature.Presentation.Screens.Wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.R
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletState
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleButton

@Composable
fun WalletsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: WalletState,
    onEvent: (WalletEvent) -> Unit,
    recordEvent: (RecordEvent) -> Unit,
) {
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    WalletDetailBottomSheet(
        isSheetOpen = isSheetOpen,
        onCloseBottomSheet = { isSheetOpen = it },
        state = state,
        recordEvent = recordEvent
    )
    WalletAddDialog(
        state = state, onEvent = onEvent, onSaveEffect = {
            recordEvent(RecordEvent.UpdateRecord)
        }
    )

    Column(modifier = modifier) {
        WalletHeader(
            modifier = Modifier
                .height(150.dp)
                .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large, clip = true)
                .padding(
                    vertical = 4.dp
                )
                .clip(MaterialTheme.shapes.large)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                ), state = state
        )
        SearchBar(
            searchText = state.searchQuery,
            onValueChange = {
                onEvent(
                    WalletEvent.SearchWallet(
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

        state.walletResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "You Didn't Have Any Wallet Yet",
                isNullOrEmpty = { it.isNullOrEmpty() },
                errorMessage = resource.message ?: "",
                onMessageClick = {
                    onEvent(
                        WalletEvent.ShowDialog(
                            Wallet(walletName = "")
                        )
                    )
                },
            ) { data ->
                WalletBody(
                    wallets = data,
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
                                    width = 3.dp/2,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = MaterialTheme.shapes.medium
                                ),
                            image = R.drawable.ic_add_foreground,
                            imageColor = MaterialTheme.colorScheme.onBackground,
                            onClick = {
                                onEvent(
                                    WalletEvent.ShowDialog(
                                        Wallet(walletName = "")
                                    )
                                )
                            },
                            title = "Add New Wallet",
                            titleStyle = MaterialTheme.typography.titleLarge.copy(
                                MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    onEvent = onEvent,
                    onEntityClick = {
                        isSheetOpen = true
                    },
                    onDeleteWallet = {
                        recordEvent(RecordEvent.UpdateRecord)
                    }
                )
            }
        }
    }
}

