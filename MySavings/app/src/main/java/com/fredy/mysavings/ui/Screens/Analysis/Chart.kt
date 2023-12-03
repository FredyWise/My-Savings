package com.fredy.mysavings.ui.Screens.Analysis

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.defaultColors
import com.fredy.mysavings.Util.extractProportions
import com.fredy.mysavings.Util.formatAmount


@Composable
fun <T> StatementBody(
    modifier: Modifier = Modifier,
    items: List<T>,
    amounts: (T) -> Float,
    amountsTotal: Float,
    circleLabel: String,
    legend: (@Composable (T, Color) -> Unit)? = null,
    content: @Composable (T, Color, Float) -> Unit
) {
    val itemsProportion = items.extractProportions {
        amounts(
            it
        )
    }
    val circleColors = defaultColors.subList(
        0,
        items.size,
    )
    Column(modifier = modifier) {
        Row {
            Box(
                Modifier
                    .padding(10.dp)
                    .weight(0.6f)
            ) {
                AnimatedCircle(
                    itemsProportion,
                    circleColors,
                    Modifier
                        .height(200.dp)
                        .align(
                            Alignment.Center
                        )
                        .fillMaxWidth()
                )
                Column(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                ) {
                    Text(
                        text = circleLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                    Text(
                        text = formatAmount(
                            amountsTotal.toDouble()
                        ),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }
            }
            if (legend != null) {
                LazyColumn(
                    modifier = Modifier.weight(
                        0.4f
                    )
                ) {
                    itemsIndexed(items) { index, item ->
                        legend(item, circleColors[index])
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn {
            itemsIndexed(items) { index, item ->
                content(item, circleColors[index], itemsProportion[index])
            }
        }
    }
}


@Composable
fun AnimatedCircle(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val currentState = remember {
        MutableTransitionState(
            AnimatedCircleProgress.START
        ).apply { targetState = AnimatedCircleProgress.END }
    }
    val stroke = with(LocalDensity.current) {
        Stroke(
            5.dp.toPx()
        )
    }
    val transition = updateTransition(
        currentState, label = ""
    )
    val angleOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }, label = ""
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            360f
        }
    }
    val shift by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = CubicBezierEasing(
                    0f, 0.75f, 0.35f, 0.85f
                )
            )
        }, label = ""
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            30f
        }
    }

    Canvas(modifier) {
        val innerRadius = (size.minDimension - stroke.width) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(
            innerRadius * 2, innerRadius * 2
        )
        var startAngle = shift - 90f
        proportions.forEachIndexed { index, proportion ->
            val sweep = proportion * angleOffset
            drawArc(
                color = colors[index],
                startAngle = startAngle + dividerLengthInDegrees / 2,
                sweepAngle = sweep - dividerLengthInDegrees,
                topLeft = topLeft,
                size = size,
                useCenter = false,
                style = stroke
            )
            startAngle += sweep
        }
    }
}

private enum class AnimatedCircleProgress {
    START,
    END
}

private const val dividerLengthInDegrees = 1.8f