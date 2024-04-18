package com.fredy.mysavings.Feature.Presentation.Screens.Analysis

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.RecordTypeColor
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatDateDay
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isFilterTypeMonthBelow
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordState
import com.fredy.mysavings.Feature.Presentation.Screens.Analysis.Charts.ChartLine
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.Calendar
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleEntityItem
import kotlin.math.absoluteValue

@Composable
fun AnalysisFlow(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
) {
    state.resourceData.recordsWithinTimeResource.let { resource ->
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
            val recordType = state.filterState.recordType
            val totalAmount = if (isExpense(recordType)) {
                state.balanceBar.expense.amount
            } else if (isIncome(recordType)) {
                state.balanceBar.income.amount
            } else state.balanceBar.transfer.amount
            val items = if (isExpense(recordType)) data else data.reversed()
            val contentColor = RecordTypeColor(recordType = recordType)
            LazyColumn(modifier = modifier) {
                item {
                    Box(
                        modifier = Modifier
                            .padding(
                                end = 20.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ChartLine(
                            contentColor = contentColor,
                            infoColor = RecordTypeColor(recordType = recordType),
                            pointsData = items.map { item ->
                                val date =
                                    if (state.filterState.isFilterTypeMonthBelow()) item.recordDateTime.dayOfMonth.toFloat() else item.recordDateTime.dayOfYear.toFloat()
                                Point(
                                    x = date,
                                    y = item.recordAmount.absoluteValue.toFloat(),
                                )
                            }.reversed(),
                            year = state.filterState.selectedDate.year,
                            month = state.filterState.selectedDate.monthValue,
//                            currency = items.first().recordCurrency,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .padding(top = 8.dp)
                            .clickable {
                                onEvent(
                                    RecordsEvent.ToggleRecordType
                                )
                            },
                    ) {
                        Text(
                            text = "Total " + recordType.name + ": ",
                            color = onBackgroundColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = formatBalanceAmount(
                                totalAmount
                            ),
                            color = RecordTypeColor(recordType = recordType),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    if (state.filterState.isFilterTypeMonthBelow()) {
                        Calendar(
                            inputColor = contentColor,
                            calendarInput = items.associate {
                                it.recordDateTime.dayOfMonth to it.recordAmount.toString()
                            }.toMutableMap(),
                            date = state.filterState.selectedDate,
                            title = {
                            },
                            modifier = Modifier
                                .padding(
                                    10.dp
                                )
                                .fillMaxWidth()
                                .aspectRatio(
                                    1.3f
                                ),
                        )
                    }
                }
                items(items, key = { it.recordId }) { item ->
                    Spacer(modifier = Modifier.height(8.dp))
                    SimpleEntityItem(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .border(1.dp, onBackgroundColor, MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        iconModifier = Modifier.size(30.dp),
                        icon = R.drawable.ic_calendar,
                        iconDescription = "",
                        endContent = {
                            Text(
                                text = formatBalanceAmount(
                                    item.recordAmount,
                                    item.recordCurrency,
                                    isShortenToChar = true,
                                    k = false
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                color = onBackgroundColor
                            )
                        },
                    ) {
                        CustomStickyHeader(
                            textStyle = MaterialTheme.typography.titleMedium,
                            title = formatDateDay(item.recordDateTime.toLocalDate()),
                            textColor = onBackgroundColor,
                            useDivider = false,
                            topPadding = 0.dp,
                            bottomPadding = 0.dp,
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(75.dp)) }
            }

        }
    }
}
