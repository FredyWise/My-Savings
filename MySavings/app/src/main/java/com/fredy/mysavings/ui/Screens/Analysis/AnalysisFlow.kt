package com.fredy.mysavings.ui.Screens.Analysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.ViewModels.AnalysisState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import com.fredy.mysavings.ui.Screens.Analysis.Charts.ChartLine
import com.fredy.mysavings.ui.Screens.ZCommonComponent.Calendar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import kotlin.math.absoluteValue

@Composable
fun AnalysisFlow(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    state: AnalysisState,
    onEvent: (AnalysisEvent) -> Unit,
) {
    val key = state.analysisData.recordsWithinTimeResource.hashCode()
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
        state.analysisData.recordsWithinTimeResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "There is no ${state.filterState.recordType.name} on this date yet",
                errorMessage = resource.message ?: "",
                isNullOrEmpty = { it.isNullOrEmpty() },
                onMessageClick = {
                    onEvent(
                        AnalysisEvent.ToggleRecordType
                    )
                    isVisible.targetState = false
                },
            ) { data ->
                val items = if (isExpense(data.first().recordType)) data else data.reversed()

                Column {
                    Box(
                        modifier = Modifier
                            .weight(
                                1f
                            )
                            .padding(
                                end = 20.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ChartLine(
                            pointsData = items.map { item ->
                                Point(
                                    x = item.recordDateTime.dayOfMonth.toFloat(),
                                    y = item.recordAmount.absoluteValue.toFloat(),
                                )
                            }.reversed(),
                            currency = items.first().recordCurrency,
                        )
                    }
                    Calendar(
                        calendarInput = items.associate {
                            it.recordDateTime.dayOfMonth to it.recordAmount.toString()
                        }.toMutableMap(),
                        date = state.filterState.selectedDate,
                        title = {
                            Text(
                                text = state.filterState.recordType.name + ": " + formatRangeOfDate(
                                    state.filterState.selectedDate, state.filterState.filterType
                                ),
                                modifier = Modifier.clickable {
                                    onEvent(
                                        AnalysisEvent.ToggleRecordType
                                    )
                                },
                                color = onBackgroundColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        modifier = Modifier
                            .padding(
                                10.dp
                            )
                            .fillMaxWidth()
                            .aspectRatio(
                                1.3f
                            ),
                        calendarExpenseInputColor = MaterialTheme.colorScheme.primary,
                        calendarIncomeInputColor = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}
