package com.fredy.mysavings.ui.Screens.Other

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.ViewModels.CurrencyState
import com.fredy.mysavings.ViewModels.Event.CurrencyEvent
import com.fredy.mysavings.ui.Screens.ZCommonComponent.AsyncImageHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CurrencyDropdown
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleDialog

@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    rootNavController: NavController,
    title: String,
    state: CurrencyState,
    onEvent: (CurrencyEvent) -> Unit
) {
    state.currency?.let { currency ->
        SimpleDialog(
            title = currency.name,
            onDismissRequest = { onEvent(CurrencyEvent.HideDialog) },
            onSaveClicked = { onEvent(CurrencyEvent.SaveCurrency) }) {
            TextField(
                modifier = Modifier.height(50.dp),
                value = state.updatedValue,
                onValueChange = { onEvent(CurrencyEvent.UpdateValue(it)) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
        }
    }
    DefaultAppBar(
        modifier = modifier, title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        Column(
        ) {
            CurrencyItem(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                name = "Base Currency",
                leadingComponent = {
                    CurrencyDropdown(
                        modifier = Modifier.weight(1f),
                        selectedText = state.userData.userCurrency,
                        onClick = { onEvent(CurrencyEvent.BaseCurrency(it)) }
                    )
                },
            )
            ResourceHandler(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(
                        horizontal = 8.dp
                    ),
                resource = state.currenciesResource,
                isNullOrEmpty = { it.isNullOrEmpty() },
                onMessageClick = { },
            ) { data ->
                LazyColumn() {
                    item {
                        Spacer(modifier = Modifier.width(4.dp))
                        CurrencyConverter(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            fromCurrency = state.fromCurrency,
                            fromOnClick = { onEvent(CurrencyEvent.FromCurrency(it)) },
                            toCurrency = state.toCurrency,
                            toOnClick = { onEvent(CurrencyEvent.ToCurrency(it)) },
                            fromValue = state.fromValue,
                            onFromValueChange = { onEvent(CurrencyEvent.FromValue(it)) },
                            toValue = state.toValue,
                            onToValueChange = { onEvent(CurrencyEvent.ToValue(it)) }
                        )
                    }
                    items(data, key = { it.code }) {
                        Spacer(modifier = Modifier.width(4.dp))
                        CurrencyItem(
                            modifier = Modifier
                                .height(50.dp)
                                .clickable { onEvent(CurrencyEvent.ShowDialog(it)) },
                            imageUrl = it.url,
                            contentDescription = it.alt,
                            name = it.name,
                            code = it.code,
                            leadingComponent = {
                                Text(
                                    text = formatBalanceAmount(it.value, it.symbol),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = onBackgroundColor,
                                    modifier = Modifier.padding(
                                        vertical = 3.dp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyConverter(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    fromCurrency: String,
    fromOnClick: (String) -> Unit,
    toCurrency: String,
    toOnClick: (String) -> Unit,
    fromValue: String,
    onFromValueChange: (String) -> Unit,
    toValue: String,
    onToValueChange: (String) -> Unit,

    ) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CurrencyDropdown(
                selectedText = fromCurrency,
                textFieldColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                ),
                onClick = fromOnClick
            )
            TextField(
                modifier = Modifier.height(50.dp),
                value = fromValue,
                onValueChange = onFromValueChange,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
        }
        Column(
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CurrencyDropdown(
                selectedText = toCurrency,
                textFieldColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                ),
                onClick = toOnClick
            )
            TextField(
                modifier = Modifier.height(50.dp),
                value = toValue,
                onValueChange = onToValueChange,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
        }
    }
}

@Composable
fun CurrencyItem(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    contentDescription: String = "",
    name: String,
    code: String = "",
    leadingComponent: @Composable () -> Unit,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImageHandler(
            modifier = Modifier.size(50.dp),
            imageUrl = imageUrl,
            imageScale = ContentScale.Fit,
            contentDescription = contentDescription,
            imageVector = Icons.Default.CurrencyExchange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .weight(1f, fill = false),
                maxLines = Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
            )
            if (code.isNotEmpty()) {
                Text(
                    text = " | $code",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onBackgroundColor,
                    modifier = Modifier
                        .padding(vertical = 3.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        leadingComponent()
    }
}