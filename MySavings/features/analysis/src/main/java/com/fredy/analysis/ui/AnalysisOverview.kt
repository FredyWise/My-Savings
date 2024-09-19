package com.fredy.analysis.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.analysis.ui.Charts.ChartSlimDonutWithTitle
import com.fredy.analysis.viewModel.AnalysisEvent
import com.fredy.analysis.viewModel.AnalysisState
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enumsChecker.isExpense
import com.fredy.domain.enumsChecker.recordTypeColor
import com.fredy.domain.model.Category
import com.fredy.domain.util.SavingsStrings

import com.fredy.ui.components.handler.ResourceHandler
import com.fredy.ui.components.list.SimpleEntityItem
import com.fredy.theme.util.BalanceColor
import com.fredy.theme.util.defaultColors
import com.fredy.theme.util.formatBalanceAmount


@Composable
fun AnalysisOverview(
    modifier: Modifier = Modifier,
    state: AnalysisState,
    onEvent: (AnalysisEvent) -> Unit,
    onGetCategoryDetails: (Category) -> Unit,
) {

    state.resourceData.categoriesWithAmountResource.let { resource ->
        ResourceHandler(
            resource = resource,
            nullOrEmptyMessage = "There is no ${state.filterState.recordType.name} on this date yet",
            isNullOrEmpty = { it.isNullOrEmpty() },
            onMessageClick = {
                onEvent(
                    AnalysisEvent.ToggleAnalysisType
                )
            },
        ) { data ->
            val recordType = state.filterState.recordType
            val items = if (recordType.isExpense()) data.reversed() else data
            val totalAmount = when (recordType) {
                RecordType.Expense -> state.balanceBar.expense.amount
                RecordType.Income -> state.balanceBar.income.amount
                RecordType.Transfer -> state.balanceBar.transfer.amount
            }
            val contentColor = recordType.recordTypeColor()
            val colors =
                (if (recordType.isExpense()) defaultColors else defaultColors.reversed()).subList(
                    0,
                    items.size
                )
            val itemsProportion = items.extractProportions { proportion ->
                proportion.amount.toFloat()
            }
            LazyColumn(modifier = modifier) {
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
                                    SavingsStrings.total
                                ) + " " + items.first().category.categoryType.name,
                                labelColor = contentColor,
                                amountsTotal = totalAmount,
                                onClickLabel = {
                                    onEvent(
                                        AnalysisEvent.ToggleAnalysisType
                                    )
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
                                        text = categoryWithAmount.category.categoryName + " | " + categoryWithAmount.currency,
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
                    HorizontalDivider(
                        modifier = Modifier.height(
                            0.3.dp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.4f
                        )
                    )
                    SimpleEntityItem(
                        modifier = Modifier.clickable {
                            onGetCategoryDetails(item.category)
                        },
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
                                    formatBalanceAmount(
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
                            progress = { itemsProportion[index] },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(
                                    CircleShape
                                )
                                .height(
                                    8.dp
                                ),
                            color = colors[index],
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(75.dp))
                }
            }

        }
    }
}

fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}