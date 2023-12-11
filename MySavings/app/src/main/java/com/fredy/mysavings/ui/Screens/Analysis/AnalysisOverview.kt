package com.fredy.mysavings.ui.Screens.Analysis

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
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
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.defaultColors
import com.fredy.mysavings.Util.formatAmount
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.ViewModel.AnalysisState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import com.fredy.mysavings.ui.Screens.LoadingAnimation
import com.fredy.mysavings.ui.Screens.SimpleEntityItem

@Composable
fun AnalysisOverview(
    modifier: Modifier = Modifier,
    state: AnalysisState,
    resource: ResourceState,
    onEvent: (AnalysisEvent) -> Unit,
) {
    val key = state.categoriesWithAmount.hashCode()
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
        state.categoriesWithAmount.let { categories ->
            if(categories.isNotEmpty() && categories.first().currency.isNotEmpty()) {
                val items = if (isExpense(categories.first().category.categoryType)) categories else categories.reversed()
                val colors = if (isExpense(items.first().category.categoryType)) defaultColors.subList(
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
                                            AnalysisEvent.ToggleRecordType
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
            }else{
                LoadingAnimation(
                    isLoading = resource.isLoading && categories.isNotEmpty(),
                    notLoadingMessage = "You haven't made any ${state.recordType.name} yet",
                    onClick = {
                        isVisible.targetState = false
                        onEvent(
                            AnalysisEvent.ToggleRecordType
                        )
                    }
                )
            }
        }
    }
}

fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}