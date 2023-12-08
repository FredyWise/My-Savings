package com.fredy.mysavings.ui.Screens.Analysis

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun <T> StatementBody(
    modifier: Modifier = Modifier,
    items: List<T>,
    circleColors: (List<T>) -> List<Color>,
    amounts: (T) -> Float,
    amountsTotal: Double,
    graphLabel: String,
    onClickLabel: () -> Unit,
    legend: (@Composable (T, Color) -> Unit)? = null,
    content: @Composable (T, Color, Float) -> Unit
) {
    val itemsProportion = items.extractProportions {
        amounts(
            it
        )
    }
    Column(modifier = modifier) {
        Row {
            ChartSlimDonutWithTitle(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(
                        0.6f
                    ),
                itemsProportion = itemsProportion,
                circleColors = circleColors(items),
                graphLabel = graphLabel,
                onClickLabel = onClickLabel,
                amountsTotal = amountsTotal
            )
            if (legend != null) {
                LazyColumn(
                    modifier = Modifier.weight(
                        0.4f
                    )
                ) {
                    itemsIndexed(items) { index, item ->
                        legend(
                            item,
                            circleColors(items)[index]
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn {
            itemsIndexed(items) { index, item ->
                content(
                    item,
                    circleColors(items)[index],
                    itemsProportion[index]
                )
            }

        }
    }
}

fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}