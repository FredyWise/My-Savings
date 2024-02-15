package com.fredy.mysavings.ui.Screens.Analysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.defaultColors
import com.fredy.mysavings.Util.formatAmount
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.Screens.Analysis.Charts.ChartSlimDonutWithTitle
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem

@Composable
fun AnalysisOverview(
    modifier: Modifier = Modifier,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
) {
    val key = state.resourceData.categoriesWithAmountResource.hashCode()
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
        state.resourceData.categoriesWithAmountResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "There is no ${state.filterState.recordType.name} on this date yet",
                isNullOrEmpty = { it.isNullOrEmpty() },
                errorMessage = resource.message ?: "",
                onMessageClick = {
                    isVisible.targetState = false
                    onEvent(
                        RecordsEvent.ToggleRecordType
                    )
                },
            ) { data ->                // this to bellow should be able to be simplified
                val items =
                    if (isExpense(data.first().category.categoryType)) data else data.reversed()
                val colors =
                    if (isExpense(items.first().category.categoryType)) defaultColors.subList(
                        0,
                        items.size,
                    ) else defaultColors.reversed().subList(
                        0,
                        items.size,
                    )

                val itemsProportion = items.extractProportions { proportion ->
                    proportion.amount.toFloat()
                }
                LazyColumn {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .weight(
                                        0.5f
                                    )
                                    .padding(8.dp),
                            ) {
                                ChartSlimDonutWithTitle(
                                    itemsProportion = itemsProportion,
                                    circleColors = colors,
                                    graphLabel = stringResource(
                                        R.string.total
                                    ) + " " + items.first().category.categoryType.name,
                                    amountsTotal = items.sumOf { it.amount },
                                    onClickLabel = {
                                        onEvent(
                                            RecordsEvent.ToggleRecordType
                                        )
                                        isVisible.targetState = false
                                    },
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(
                                        0.4f
                                    )
                                    .padding(
                                        bottom = 8.dp
                                    ),
                                verticalArrangement = Arrangement.Center
                            ) {
                                items.forEachIndexed { index, categoryWithAmount ->
                                    Row(
                                        modifier = Modifier.padding(
                                            top = 8.dp,
                                        ),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(
                                                    15.dp
                                                )
                                                .background(
                                                    colors[index]
                                                )
                                        )
                                        Spacer(
                                            modifier = Modifier.width(
                                                8.dp
                                            )
                                        )
                                        Text(
                                            text = categoryWithAmount.category.categoryName + " - " + categoryWithAmount.currency,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = MaterialTheme.colorScheme.onBackground,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    itemsIndexed(items) { index, item ->
                        Divider(
                            modifier = Modifier.height(
                                0.3.dp
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.4f
                            )
                        )
                        SimpleEntityItem(
                            icon = item.category.categoryIcon,
                            iconDescription = item.category.categoryIconDescription,
                            iconModifier = Modifier
                                .size(
                                    55.dp
                                )
                                .clip(
                                    shape = MaterialTheme.shapes.medium
                                ),
                            endContent = {
                                Text(
                                    modifier = Modifier.width(
                                        73.dp
                                    ),
                                    text = "${
                                        formatAmount(
                                            (itemsProportion[index] * 100).toDouble()
                                        )
                                    }%",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.category.categoryName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(
                                        0.55f
                                    )
                                )
                                Text(
                                    text = formatBalanceAmount(
                                        amount = item.amount,
                                        currency = item.currency,
                                        isShortenToChar = true
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = BalanceColor(
                                        amount = item.amount
                                    ),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(
                                        0.50f
                                    )
                                )
                            }
                            Spacer(
                                modifier = Modifier.height(
                                    5.dp
                                )
                            )
                            LinearProgressIndicator(
                                progress = itemsProportion[index],
                                color = colors[index],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        CircleShape
                                    )
                                    .height(
                                        8.dp
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}

fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}