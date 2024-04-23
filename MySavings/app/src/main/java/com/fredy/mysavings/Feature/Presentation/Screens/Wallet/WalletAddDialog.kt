package com.fredy.mysavings.Feature.Presentation.Screens.Wallet

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.DefaultData.walletIcons
import com.fredy.mysavings.Util.currencyCodes
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletState
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ChooseIcon
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDropdown

@Composable
fun WalletAddDialog(
    state: WalletState,
    onEvent: (WalletEvent) -> Unit,
    onSaveEffect: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (state.isAddingWallet) {
        SimpleDialog(
            modifier = modifier,
            dismissOnSave = false,
            title = if (state.walletId.isEmpty()) "Add New Wallet" else "Update Wallet",
            onDismissRequest = { onEvent(WalletEvent.HideDialog) },
            onSaveClicked = {
                if (state.walletName.isBlank() || state.walletAmount.isBlank() || state.walletCurrency.isBlank() || state.walletIconDescription.isBlank()) {
                    Toast.makeText(
                        context,
                        "Please Fill All Required Information!!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onEvent(WalletEvent.SaveWallet)
                    onSaveEffect()
                    onEvent(WalletEvent.HideDialog)
                }
            },
        ) {
            TextField(
                value = state.walletAmount,
                onValueChange = {
                    if (it.isEmpty() || it.matches(
                            Regex("^(\\d+\\.?\\d*|\\d*\\.\\d+)$")
                        )
                    ) {
                        onEvent(
                            WalletEvent.WalletAmount(
                                it
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp
                    ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        KeyboardActions.Default.onNext
                    }),
                label = {
                    Text(text = if (state.walletId.isEmpty()) "Initial Amount" else "Current Amount")
                },
                placeholder = {
                    Text(text = "0")
                },
            )
            SimpleDropdown(
                menuModifier = Modifier,
                list = currencyCodes,
                selectedText = state.walletCurrency,
                onClick = {
                    onEvent(
                        WalletEvent.WalletCurrency(
                            it
                        )
                    )
                },
            )
            TextField(
                value = state.walletName,
                onValueChange = {
                    onEvent(
                        WalletEvent.WalletName(it)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp
                    ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        KeyboardActions.Default.onDone
                    }),
                label = {
                    Text(text = "Name")
                },
                placeholder = {
                    Text(text = "Wallet Name")
                },
            )
            ChooseIcon(
                icons = walletIcons, onClick = {
                    onEvent(
                        WalletEvent.WalletIcon(
                            icon = it.image,
                            iconDescription = it.description,
                        )
                    )
                }, iconModifier = Modifier.clip(
                    shape = CircleShape
                ), selectedIcon = state.walletIcon
            )
        }
    }
}