package com.fredy.mysavings.ui.Screens.Account

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.RoomDatabase.Event.AccountEvent
import com.fredy.mysavings.Data.accountIcons
import com.fredy.mysavings.ViewModel.AccountState
import com.fredy.mysavings.ui.Screens.ChooseIcon
import com.fredy.mysavings.ui.Screens.SimpleAddDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAddDialog(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    SimpleAddDialog(
        modifier = modifier,
        title = if (state.accountId == null) "Add New Account" else "Update Account",
        onDismissRequest = { onEvent(AccountEvent.HideDialog) },
        onCancelClicked = { onEvent(AccountEvent.HideDialog) },
        onSaveClicked = { onEvent(AccountEvent.SaveAccount) },
    ) {
        TextField(
            value = state.accountAmount,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^(\\d+\\.?\\d*|\\d*\\.\\d+)$"))) {
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
                Text(text = "Initial Amount")
            },
            placeholder = {
                Text(text = "0")
            },
        )
        //choose currency here
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
                        iconDescription = it.description
                    )
                )
            }, iconModifier = Modifier.clip(
                shape = MaterialTheme.shapes.extraLarge
            ), selectedIcon = state.accountIcon
        )
    }
}