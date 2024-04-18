package com.fredy.mysavings.Feature.Presentation.Screens.Wallet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.WalletEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.AdvancedEntityItem
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleWarningDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalletBody(
    modifier: Modifier = Modifier,
    wallets: List<Wallet>,
    topItem: @Composable () -> Unit = {},
    onEvent: (WalletEvent) -> Unit,
    onDeleteWallet: () -> Unit,
    onEntityClick: () -> Unit,
) {
    var isShowWarning by remember { mutableStateOf(false) }
    var tempWallet by remember { mutableStateOf(Wallet()) }
    SimpleWarningDialog(
        isShowWarning = isShowWarning,
        onDismissRequest = { isShowWarning = false },
        onSaveClicked = {
            onEvent(
                WalletEvent.DeleteWallet(
                    tempWallet,
                    onDeleteWallet
                )
            )
        },
        warningText = "Are You Sure Want to Delete This Wallet?"
    )
    LazyColumn(modifier = modifier) {
        item { topItem() }
        stickyHeader {
            CustomStickyHeader(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.background
                ),
                title = "Wallets",
                textStyle = MaterialTheme.typography.titleLarge
            )
        }
        items(wallets, key = { it.walletId }) { wallet ->
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
                    .clickable {
                        onEntityClick()
                        onEvent(WalletEvent.GetWalletDetail(wallet))
                    }
                    .background(
                        MaterialTheme.colorScheme.surface
                    ),
                icon = wallet.walletIcon,
                iconDescription = wallet.walletIconDescription,
                iconModifier = Modifier
                    .size(50.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium
                    ),
                menuItems = listOf(
                    ActionWithName(
                        name = "Delete Wallet",
                        action = {
                            isShowWarning = true
                            tempWallet = wallet
                        },
                    ), ActionWithName(
                        name = "Edit Wallet",
                        action = {
                            onEvent(
                                WalletEvent.ShowDialog(
                                    wallet
                                )
                            )
                        },
                    )
                )
            ) {
                Text(
                    text = wallet.walletName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Balance: " + formatBalanceAmount(
                        amount = wallet.walletAmount,
                        currency = wallet.walletCurrency,
                        isShortenToChar = true,
                        k = false
                    ),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = BalanceColor(amount = wallet.walletAmount)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}
