package com.fredy.mysavings.Feature.Presentation.Screens.Analysis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.GroupBar
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.RecordTypeColor
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletState
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordState
import com.fredy.mysavings.Feature.Presentation.Screens.Wallet.WalletDetailBottomSheet
import com.fredy.mysavings.Feature.Presentation.Screens.Analysis.Charts.ChartGroupedBar
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleEntityItem
import kotlin.math.absoluteValue

@Composable
fun AnalysisAccount(
    modifier: Modifier = Modifier,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
    walletState: WalletState,
    accountEvent: (WalletEvent) -> Unit,
) {
    val expenseColor by remember { mutableStateOf(BalanceColor.Expense) }
    val incomeColor by remember { mutableStateOf(BalanceColor.Income) }


    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    WalletDetailBottomSheet(
        isSheetOpen = isSheetOpen,
        onCloseBottomSheet = { isSheetOpen = it },
        state = walletState,
        recordEvent = onEvent
    )

    state.resourceData.accountsWithAmountResource.let { resource ->
        ResourceHandler(
            resource = resource,
            nullOrEmptyMessage = "There is no ${state.filterState.recordType.name} on this date yet",
            errorMessage = resource.message ?: "",
            isNullOrEmpty = { it.isNullOrEmpty() },
            onMessageClick = {
                onEvent(
                    RecordsEvent.ToggleRecordType
                )
            },
        ) { data ->
            LazyColumn(modifier = modifier) {
                item {
                    ChartGroupedBar(
                        incomeColor = incomeColor,
                        expenseColor = expenseColor,
                        infoColor = RecordTypeColor(recordType = state.filterState.recordType),
                        groupBarData = data.mapIndexed { index, item ->
                            GroupBar(
                                label = item.wallet.walletName,
                                barList = listOf(
                                    BarData(
                                        Point(
                                            (index + 1).toFloat(),
                                            item.expenseAmount.absoluteValue.toFloat()
                                        ),
                                        label = "B1",
                                        description = "Bar at ${(index + 1)} with label B1 has value ${
                                            String.format(
                                                "%.2f",
                                                item.expenseAmount
                                            )
                                        }",

                                        ),
                                    BarData(
                                        Point(
                                            (index + 1).toFloat(),
                                            item.incomeAmount.absoluteValue.toFloat()
                                        ),
                                        label = "B2",
                                        description = "Bar at ${(index + 1)} with label B2 has value ${
                                            String.format(
                                                "%.2f",
                                                item.incomeAmount
                                            )
                                        }"
                                    )
                                )
                            )
                        },
                    )
                }
                items(data, key = { it.wallet.walletId }) { item ->
                    Divider(
                        modifier = Modifier.height(
                            0.3.dp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.4f
                        )
                    )
                    SimpleEntityItem(
                        modifier = Modifier.clickable {
                            accountEvent(WalletEvent.GetWalletDetail(item.wallet))
                            isSheetOpen = true
                        },
                        icon = item.wallet.walletIcon,
                        iconDescription = item.wallet.walletIconDescription,
                        iconModifier = Modifier
                            .size(
                                55.dp
                            )
                            .clip(
                                shape = MaterialTheme.shapes.medium
                            ),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = item.wallet.walletName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Balance:",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = formatBalanceAmount(
                                        amount = item.expenseAmount,
                                        currency = item.wallet.walletCurrency,
                                        isShortenToChar = true
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = expenseColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(
                                        0.50f
                                    )
                                )
                                Text(
                                    text = formatBalanceAmount(
                                        amount = item.incomeAmount,
                                        currency = item.wallet.walletCurrency,
                                        isShortenToChar = true
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = incomeColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(
                                        0.50f
                                    )
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(75.dp))
                }
            }
        }
    }
}