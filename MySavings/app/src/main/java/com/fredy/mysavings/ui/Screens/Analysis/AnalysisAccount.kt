package com.fredy.mysavings.ui.Screens.Analysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.ViewModels.AnalysisState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import com.fredy.mysavings.ui.Screens.Analysis.Charts.ChartGroupedBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem
import kotlin.math.absoluteValue

@Composable
fun AnalysisAccount(
    modifier: Modifier = Modifier,
    state: AnalysisState,
    onEvent: (AnalysisEvent) -> Unit,
) {

    val key = state.categoriesWithAmountResource.hashCode()
    val isVisible = remember(key) {
        MutableTransitionState(
            false
        ).apply { targetState = true }
    }
    AnimatedVisibility(
        modifier = modifier,
        visibleState = isVisible,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 500
            ),
            initialOffsetY = { fullHeight -> fullHeight },
        ) + fadeIn(),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = 500
            ),
            targetOffsetY = { fullHeight -> fullHeight },
        ) + fadeOut()
    ) {
        state.accountsWithAmountResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "There is no ${state.recordType.name} on this date yet",
                errorMessage = resource.message ?: "",
                isNullOrEmpty = { it.isNullOrEmpty() },
                onMessageClick = {
                    onEvent(
                        AnalysisEvent.ToggleRecordType
                    )
                    isVisible.targetState = false
                },
            ) { data ->
                LazyColumn {
                    item {
                        ChartGroupedBar(
                            groupBarData = data.mapIndexed { index, item ->
                                GroupBar(
                                    label = item.account.accountName,
                                    barList = listOf(
                                        BarData(
                                            Point(
                                                (index+1).toFloat(),
                                                item.expenseAmount.absoluteValue.toFloat()
                                            ),
                                            label = "B1",
                                            description = "Bar at ${(index+1)} with label B1 has value ${
                                                String.format(
                                                    "%.2f",
                                                    item.expenseAmount
                                                )
                                            }"
                                        ),
                                        BarData(
                                            Point(
                                                (index+1).toFloat(),
                                                item.incomeAmount.absoluteValue.toFloat()
                                            ),
                                            label = "B2",
                                            description = "Bar at ${(index+1)} with label B2 has value ${
                                                String.format(
                                                    "%.2f",
                                                    item.incomeAmount
                                                )
                                            }"
                                        )
                                    )
                                )
                            },
                            currency = data.first().account.accountCurrency,
                        )
                    }
                    itemsIndexed(data) { index, item ->
                        Divider(
                            modifier = Modifier.height(
                                0.3.dp
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.4f
                            )
                        )
                        SimpleEntityItem(
                            icon = item.account.accountIcon,
                            iconDescription = item.account.accountIconDescription,
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
                                    text = item.account.accountName,
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
                                        text = "This Period:",
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
                                            currency = item.account.accountCurrency,
                                            isShortenToChar = true
                                        ),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(
                                            0.50f
                                        )
                                    )
                                    Text(
                                        text = formatBalanceAmount(
                                            amount = item.incomeAmount,
                                            currency = item.account.accountCurrency,
                                            isShortenToChar = true
                                        ),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = BalanceColor(
                                            amount = item.incomeAmount
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(
                                            0.50f
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}