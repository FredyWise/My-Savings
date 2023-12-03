package com.fredy.mysavings.ui.Screens.Analysis


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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.formatAmount
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatRangeOfDate
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
    onEvent: (AnalysisEvent) -> Unit,
) {
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
            selectedData = formatRangeOfDate(
                state.chosenDate, state.filterType
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
        StatementBody(
            modifier = modifier,
            items = state.categoriesWithAmount,
            amounts = { categoriesWithAmount -> categoriesWithAmount.amount.toFloat() },
            amountsTotal = state.totalExpense.toFloat(),
            circleLabel = stringResource(R.string.total),
            legend = { categoriesWithAmount, color ->
                Row(
                    modifier = Modifier.padding(
                        top = 10.dp, end = 10.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(
                                15.dp
                            )
                            .background(color)
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
            content = { categoriesWithAmount, color, proportion ->
                Divider(
                    modifier = Modifier.height(0.3.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.4f
                    )
                )
                SimpleEntityItem(icon = categoriesWithAmount.category.categoryIcon,
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
                            text = "${
                                formatAmount(
                                    (proportion * 100).toDouble()
                                )
                            }%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }) {
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
            },
        )
    }

}

