package com.fredy.mysavings.ui.Screens.Analysis.Charts

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.formatCharAmount

@Composable
fun ChartSlimDonutWithTitle(
    modifier: Modifier = Modifier,
    itemsProportion: List<Float>,
    circleColors: List<Color>,
    graphLabel: String,
    onClickLabel: () -> Unit,
    amountsTotal: Double,
) {
    Box(
        modifier = modifier
            .clip(
                CircleShape
            )
            .clickable {
                onClickLabel()
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = graphLabel,
                style = MaterialTheme.typography.bodyLarge,
                color = BalanceColor(amount = amountsTotal),
            )
            Text(
                text = formatCharAmount(
                    amountsTotal
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = BalanceColor(amount = amountsTotal),
            )
        }
        ChartSlimDonut(
            itemsProportion,
            circleColors,
            Modifier.size(200.dp)
        )
    }
}

@Composable
fun ChartSlimDonut(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val key = colors.hashCode()
    val currentState = remember(key) {
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
                delayMillis = 100,
                durationMillis = 1100,
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
                delayMillis = 100,
                durationMillis = 1100,
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