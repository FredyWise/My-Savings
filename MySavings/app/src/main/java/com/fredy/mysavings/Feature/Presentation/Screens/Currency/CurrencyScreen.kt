package com.fredy.mysavings.Feature.Presentation.Screens.Currency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fredy.mysavings.Util.currencyCodes
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Feature.Presentation.ViewModels.CurrencyState
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.CurrencyEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDropdown

@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    rootNavController: NavController,
    title: String,
    state: CurrencyState,
    onEvent: (CurrencyEvent) -> Unit,
    updateMainScreen: () -> Unit
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
        Column() {
            CurrencyItem(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .clickable { },
                name = "Base Currency",
                leadingComponent = {
                    SimpleDropdown(
                        modifier = Modifier.weight(1f),
                        list = currencyCodes,
                        selectedText = state.userData.userCurrency,
                        onClick = {
                            onEvent(CurrencyEvent.BaseCurrency(it))
                            updateMainScreen()
                        }
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
                nullOrEmptyMessage = "You need to be online first",
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

