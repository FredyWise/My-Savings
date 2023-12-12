package com.fredy.mysavings.ui.Screens.Analysis

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.ViewModel.AnalysisState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import com.fredy.mysavings.ui.Screens.ZCommonComponent.LoadingAnimation
import kotlin.math.absoluteValue

@Composable
fun AnalysisFlow(
    modifier: Modifier = Modifier,
    state: AnalysisState,
    resource: ResourceState,
    onEvent: (AnalysisEvent) -> Unit,
) {
    val key = state.recordsWithinTime.hashCode()
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
            if (categories.isNotEmpty() && categories.first().currency.isNotEmpty()) {
                val items = if (isExpense(
                        categories.first().category.categoryType
                    )) categories else categories.reversed()
                Box(
                    modifier = Modifier.padding(
                        end = 20.dp
                    )
                ) {
                    ChartLine(
                        pointsData = state.recordsWithinTime.map { item ->
                            Point(
                                x = item.recordDateTime.dayOfMonth.toFloat(),
                                y = item.recordAmount.absoluteValue.toFloat(),
                            )
                        }.reversed(),
                    )
                }
            } else {
                LoadingAnimation(isLoading = resource.isLoading && categories.isNotEmpty(),
                    notLoadingMessage = "You haven't made any ${state.recordType.name} yet",
                    onClick = {
                        onEvent(
                            AnalysisEvent.ToggleRecordType
                        )
                        isVisible.targetState = false
                    })
            }
        }
    }
}

