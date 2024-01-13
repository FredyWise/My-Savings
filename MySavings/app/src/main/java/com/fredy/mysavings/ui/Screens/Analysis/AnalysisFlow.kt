package com.fredy.mysavings.ui.Screens.Analysis

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
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.AnalysisState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import com.fredy.mysavings.ui.Screens.Analysis.Charts.ChartLine
import com.fredy.mysavings.ui.Screens.ZCommonComponent.LoadingAnimation
import kotlin.math.absoluteValue

@Composable
fun AnalysisFlow(
    modifier: Modifier = Modifier,
    state: AnalysisState,
    onEvent: (AnalysisEvent) -> Unit,
) {
    val key = state.recordsWithinTimeResource.hashCode()
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
        state.recordsWithinTimeResource.let { resource ->
            if (resource is Resource.Success && !resource.data.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.padding(
                        end = 20.dp
                    )
                ) {
                    ChartLine(
                        pointsData = resource.data.map { item ->
                            Point(
                                x = item.recordDateTime.dayOfMonth.toFloat(),
                                y = item.recordAmount.absoluteValue.toFloat(),
                            )
                        }.reversed(),
                    )

                }
            } else {
                LoadingAnimation(
                    isLoading = resource is Resource.Loading,
                    notLoadingMessage = "You haven't have any ${state.recordType.name} on this date",
                    onClick = {
                        onEvent(
                            AnalysisEvent.ToggleRecordType
                        )
                        isVisible.targetState = false
                    },
                )
            }
        }
    }
}

