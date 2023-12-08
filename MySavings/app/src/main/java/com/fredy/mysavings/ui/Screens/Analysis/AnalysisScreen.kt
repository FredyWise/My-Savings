package com.fredy.mysavings.ui.Screens.Analysis


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.defaultColors
import com.fredy.mysavings.Util.formatAmount
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.ViewModel.AnalysisState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import com.fredy.mysavings.ui.Screens.Record.BalanceBar
import com.fredy.mysavings.ui.Screens.Record.DisplayBar
import com.fredy.mysavings.ui.Screens.Record.FilterDialog
import com.fredy.mysavings.ui.Screens.SimpleEntityItem

@Composable
fun AnalysisScreen(
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
    Column(
        modifier = modifier,
    ) {
        if (state.isChoosingFilter) {
            FilterDialog(
                title = "DisplayOption",
                selectedName = state.filterType.name,
                onDismissRequest = {
                    onEvent(
                        AnalysisEvent.HideFilterDialog
                    )
                },
                onEvent = {
                    onEvent(
                        AnalysisEvent.FilterRecord(
                            it
                        )
                    )
                },
            )
        }
        DisplayBar(
            selectedDate = state.selectedDate,
            onDateChange = {
                onEvent(
                    AnalysisEvent.ChangeDate(it)
                )
            },
            selectedTitle = formatRangeOfDate(
                state.selectedDate,
                state.filterType
            ),
            onPrevious = { onEvent(AnalysisEvent.ShowPreviousList) },
            onNext = { onEvent(AnalysisEvent.ShowNextList) },
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.extraLarge
                        )
                        .clickable {

                        }
                        .padding(4.dp),
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.extraLarge
                        )
                        .clickable {
                            onEvent(AnalysisEvent.ShowFilterDialog)
                        }
                        .padding(4.dp),
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
        )
        BalanceBar(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface
                )
                .padding(vertical = 5.dp),
            amountBars = listOf(
                BalanceItem(
                    name = "EXPENSE",
                    amount = state.totalExpense
                ),
                BalanceItem(
                    name = "INCOME",
                    amount = state.totalIncome
                ),
                BalanceItem(
                    name = "BALANCE",
                    amount = state.totalAll
                ),
            )
        )
        Log.e(
            TAG,
            "AnalysisScreen: " + state.categoriesWithAmount,
        )
        AnimatedVisibility(
            visibleState = isVisible,
            exit = slideOutVertically()
        ) {
            state.categoriesWithAmount.firstOrNull()?.let {
                StatementBody(
                    modifier = modifier,
                    items = if (isExpense(it.category.categoryType)) state.categoriesWithAmount else state.categoriesWithAmount.reversed(),
                    circleColors = { items ->
                        if (isExpense(it.category.categoryType)) {
                            defaultColors.subList(
                                0,
                                items.size,
                            )
                        } else {
                            defaultColors.reversed().subList(
                                0,
                                items.size,
                            )
                        }
                    },
                    amounts = { categoriesWithAmount -> categoriesWithAmount.amount.toFloat() },
                    amountsTotal = if (isExpense(
                            it.category.categoryType
                        )) state.totalExpense else state.totalIncome,
                    graphLabel = stringResource(R.string.total) + " " + it.category.categoryType.name,
                    onClickLabel = {
                        onEvent(AnalysisEvent.ToggleRecordType)
                    },
                    legend = { categoriesWithAmount, color ->
                        Row(
                            modifier = Modifier.padding(
                                top = 10.dp,
                                end = 10.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(
                                        15.dp
                                    )
                                    .background(
                                        color
                                    )
                            )
                            Spacer(
                                modifier = Modifier.width(
                                    8.dp
                                )
                            )
                            Text(
                                text = categoriesWithAmount.category.categoryName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    },
                ){ categoriesWithAmount, color, proportion ->
                    Divider(
                        modifier = Modifier.height(
                            0.3.dp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.4f
                        )
                    )
                    SimpleEntityItem(
                        icon = categoriesWithAmount.category.categoryIcon,
                        iconDescription = categoriesWithAmount.category.categoryIconDescription,
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
                                    70.dp
                                ),
                                text = "${
                                    formatAmount(
                                        (proportion * 100).toDouble()
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
                                text = categoriesWithAmount.category.categoryName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = formatBalanceAmount(
                                    amount = categoriesWithAmount.amount,
                                    currency = categoriesWithAmount.currency
                                ),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = BalanceColor(
                                    amount = categoriesWithAmount.amount
                                )
                            )
                        }
                        LinearProgressIndicator(
                            progress = proportion,
                            color = color,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    8.dp
                                ),
                        )
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (resource.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(
                                40.dp
                            ),
                            strokeWidth = 4.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        Text(
                            text = "You haven't made any ${state.recordType.name} yet",
                            modifier = Modifier
                                .clickable {
                                    onEvent(
                                        AnalysisEvent.ToggleRecordType
                                    )
                                }
                                .padding(
                                    20.dp
                                ),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }

}

