package com.fredy.mysavings.Feature.Presentation.Screens.Record.Analysis.Charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.LegendLabel
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.ui.barchart.GroupBarChart
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.barchart.models.GroupBarChartData
import co.yml.charts.ui.barchart.models.GroupSeparatorConfig
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import com.fredy.mysavings.Feature.Presentation.Util.formatBalanceAmount
import com.fredy.mysavings.Feature.Presentation.Util.truncateString
import kotlin.math.absoluteValue


@Composable
fun ChartGroupedBar(
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.secondary,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    infoBackgroundColor: Color = MaterialTheme.colorScheme.background,
    infoColor: Color = MaterialTheme.colorScheme.onSecondary,
    incomeColor: Color = MaterialTheme.colorScheme.primary,
    expenseColor: Color = MaterialTheme.colorScheme.tertiary,
    groupBarData: List<GroupBar>,
) {

    val step = 5

    val xAxisData = AxisData.Builder().backgroundColor(
        backgroundColor
    ).axisLabelColor(
        gridColor
    ).axisLineColor(gridColor).axisStepSize(
        30.dp
    ).bottomPadding(5.dp).startDrawPadding(1.dp)
        .labelData { index -> groupBarData[index].label.truncateString(10) }.build()

    val yAxisData = AxisData.Builder().backgroundColor(
        backgroundColor
    ).axisLabelColor(
        contentColor
    ).axisLineColor(gridColor).steps(
        step
    ).labelAndAxisLinePadding(20.dp).axisOffset(
        20.dp
    ).labelData { index ->
        formatBalanceAmount(
            (index * (groupBarData.maxOf { data -> data.barList.maxOf { it.point.y.absoluteValue } } / step).toDouble()),
             isShortenToChar = true,
        )
    }.build()

    val colorPaletteList = listOf(
        expenseColor,
        incomeColor,
    )

    val legendsConfig = LegendsConfig(
        legendLabelList = listOf(
            LegendLabel(
                color = expenseColor,
                name = "Expense"
            ),
            LegendLabel(
                color = incomeColor,
                name = "Income"
            ),
        ),
        gridColumnCount = groupBarData.first().barList.size,
        textStyle = TextStyle(color = contentColor)
    )
    val groupBarPlotData = BarPlotData(
        groupBarList = groupBarData,
        barStyle = BarStyle(barWidth = 25.dp, selectionHighlightData = SelectionHighlightData(
            highlightTextBackgroundColor = infoBackgroundColor,
            highlightTextColor = infoColor,
            groupBarPopUpLabel = { x, y ->
                val xLabel = "name : ${groupBarData[x.toInt()-1].label} "
                val yLabel = "amount : ${
                    formatBalanceAmount(
                        y.toDouble()
                    )
                }"
                "$xLabel $yLabel"
            },
        )),
        barColorPaletteList = colorPaletteList
    )
    val data = GroupBarChartData(
        barPlotData = groupBarPlotData,
        backgroundColor = backgroundColor,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        groupSeparatorConfig = GroupSeparatorConfig(
            0.dp
        ),
        horizontalExtraSpace = 50.dp,
        tapPadding = 30.dp,
        paddingEnd = 0.dp,
        paddingBetweenStackedBars = 0.dp,
    )
    Column(
        modifier.height(300.dp)
    ) {
        Legends(
            legendsConfig = legendsConfig
        )
        GroupBarChart(
            modifier = Modifier
                .background(
                    backgroundColor
                )
                .height(250.dp)
                .padding(top = 5.dp),
            groupBarChartData = data
        )
    }
}