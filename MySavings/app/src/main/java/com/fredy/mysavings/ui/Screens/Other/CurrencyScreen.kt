package com.fredy.mysavings.ui.Screens.Other

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fredy.mysavings.ViewModels.CurrencyState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.AsyncImageHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler

@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    rootNavController: NavController,
    title: String,
    state: CurrencyState,
//    onEvent: (Currency) -> Unit
) {
    val context = LocalContext.current
    DefaultAppBar(
        modifier = modifier, title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        Column(
            modifier = modifier
                .padding(top = 8.dp)
                .padding(
                    horizontal = 8.dp
                )
        ) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImageHandler(imageVector = Icons.Default.CurrencyExchange)
                Text(
                    text = "Base Currency",
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
            ResourceHandler(
                resource = state.currenciesResource,
                isNullOrEmpty = { it.isNullOrEmpty() },
                onMessageClick = { /*TODO*/ },
            ) { data ->
                LazyColumn() {
                    items(data, key = { it.code }) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImageHandler(
                                modifier = Modifier.width(50.dp),
                                imageUrl = it.url,
                                contentDescription = it.alt,
                                imageVector = Icons.Default.CurrencyExchange
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = it.name + " | " + it.code,
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
                    }
                }
            }
        }
    }
}