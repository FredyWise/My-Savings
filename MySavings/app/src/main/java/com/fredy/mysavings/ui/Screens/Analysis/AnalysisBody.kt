package com.fredy.mysavings.ui.Screens.Analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun <T> AnalysisBody(//currently didnt find any usage
    modifier: Modifier = Modifier,
    chartModifier: Modifier = Modifier,
    items: List<T>,
    colors: (List<T>) -> List<Color>,
    amounts: (T) -> Float,
    chart: @Composable ( List<T>, List<Float>,List<Color>) -> Unit,
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
            Box(
                modifier = chartModifier
                    .weight(
                        0.5f
                    ),
            ) {
                chart(
                    items,
                    itemsProportion,
                    colors(items),
                )
            }
            if (legend != null) {
                LazyColumn(
                    modifier = Modifier.weight(
                        0.4f
                    ),
                    verticalArrangement = Arrangement.Center
                ) {
                    itemsIndexed(items) { index, item ->
                        legend(
                            item,
                            colors(items)[index]
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
                    colors(items)[index],
                    itemsProportion[index]
                )
            }

        }
    }
}
