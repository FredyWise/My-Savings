package com.fredy.mysavings.ui.Screens.Account

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.accountIcons
import com.fredy.mysavings.ViewModels.AccountState
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ChooseIcon
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CurrencyDropdown
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleDialog

@Composable
fun AccountAddDialog(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
    onSaveEffect: ()->Unit = {},
    modifier: Modifier = Modifier
) {
    SimpleDialog(
        modifier = modifier,
        title = if (state.accountId.isEmpty()) "Add New Account" else "Update Account",
        onDismissRequest = { onEvent(AccountEvent.HideDialog) },
        onSaveClicked = { onEvent(AccountEvent.SaveAccount) },
    ) {
        TextField(
            value = state.accountAmount,
            onValueChange = {
                if (it.isEmpty() || it.matches(
                        Regex("^(\\d+\\.?\\d*|\\d*\\.\\d+)$")
                    )) {
                    onEvent(
                        AccountEvent.AccountAmount(
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
                Text(text = if (state.accountId.isEmpty()) "Initial Amount" else "Current Amount")
            },
            placeholder = {
                Text(text = "0")
            },
        )
        CurrencyDropdown(
            menuModifier = Modifier.height(250.dp),
            selectedText = state.accountCurrency,
            onClick = {
                onEvent(
                    AccountEvent.AccountCurrency(
                        it
                    )
                )
            },
        )
        TextField(
            value = state.accountName,
            onValueChange = {
                onEvent(
                    AccountEvent.AccountName(it)
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
                Text(text = "Account Name")
            },
        )
        ChooseIcon(
            icons = accountIcons, onClick = {
                onEvent(
                    AccountEvent.AccountIcon(
                        icon = it.image,
                        iconDescription = it.description,
                    )
                )
            }, iconModifier = Modifier.clip(
                shape = CircleShape
            ), selectedIcon = state.accountIcon
        )
    }
}