package com.fredy.mysavings.ui.Screens.Analysis.Charts

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
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.truncateString
import kotlin.math.absoluteValue


@Composable
fun ChartGroupedBar(
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    incomeColor: Color = MaterialTheme.colorScheme.primary,
    expenseColor: Color = MaterialTheme.colorScheme.tertiary,
    groupBarData: List<GroupBar>,
) {

    val step = 5

    val xAxisData = AxisData.Builder().backgroundColor(
        backgroundColor
    ).axisLabelColor(
        contentColor
    ).axisLineColor(contentColor).axisStepSize(
        30.dp
    ).bottomPadding(5.dp).startDrawPadding(50.dp)
        .labelData { index -> truncateString(groupBarData[index].label, 10) }.build()

    val yAxisData = AxisData.Builder().backgroundColor(
        backgroundColor
    ).axisLabelColor(
        contentColor
    ).axisLineColor(contentColor).steps(
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
        incomeColor,
        expenseColor
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
        barStyle = BarStyle(barWidth = 25.dp),
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
        paddingEnd = 0.dp,
        paddingBetweenStackedBars = 0.dp
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