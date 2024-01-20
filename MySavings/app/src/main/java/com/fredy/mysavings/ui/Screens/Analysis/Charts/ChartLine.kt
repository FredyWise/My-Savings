package com.fredy.mysavings.ui.Screens.Analysis.Charts

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.formatCharAmount
import kotlin.math.nextUp

@Composable
fun ChartLine(
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.secondary,
    infoColor: Color = MaterialTheme.colorScheme.onSecondary,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    pointsData: List<Point>,
    currency: String,
) {
    val points = pointsData.sortedBy { it.x }.toMutableList()
    points.add(0, Point(0f, 0f))

    Log.e(
        TAG,
        "ChartLine: " + points + " length:" + points.size
    )
    val steps = 5

    val xAxisData = AxisData.Builder().axisStepSize(
        75.dp
    ).backgroundColor(backgroundColor).axisLabelColor(
        contentColor
    ).axisLineColor(contentColor).topPadding(
        105.dp
    ).steps(31).labelData { i -> (i).toString() }.labelAndAxisLinePadding(
        15.dp
    ).build()

    val yAxisData = AxisData.Builder().steps(steps).labelAndAxisLinePadding(
        35.dp
    ).backgroundColor(backgroundColor).axisLabelColor(
        contentColor
    ).axisLineColor(contentColor).labelData { i ->
        Log.e(TAG, "ChartLine: i=" + i)
//        val yMin = points.minOf { it.y }
        val yMax = points.maxOf { it.y }.nextUp()
//        val yScale = (yMax - yMin) / steps
        val yScale = yMax / steps
        formatCharAmount((i * yScale).toDouble())+ " $currency"
//        formatCharAmount(((i * yScale) + yMin).toDouble())
    }.build()

    val data = LineChartData(
        linePlotData = LinePlotData(
            plotType = PlotType.Line,
            lines = listOf(
                Line(
                    dataPoints = points,
                    lineStyle = LineStyle(
                        color = contentColor,
                        lineType = LineType.SmoothCurve()
                    ),
                    intersectionPoint = IntersectionPoint(
                        color = contentColor
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = gridColor
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                contentColor,
                                Color.Transparent
                            )
                        )
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = gridColor,
                        labelColor = infoColor,
                        popUpLabel = { x, y ->
                            val xLabel = "date : ${x.toInt()} "
                            val yLabel = "amount : ${
                                formatCharAmount(
                                    y.toDouble()
                                )
                            }"
                            "$xLabel $yLabel"
                        },
                    )
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(
            color = gridColor,
            enableVerticalLines = false,
        ),
        backgroundColor = backgroundColor,
        paddingRight = 0.dp,
        paddingTop = 35.dp,
        bottomPadding = 0.dp,
        containerPaddingEnd = 55.dp
    )

    LineChart(
        modifier = modifier
            .fillMaxWidth()
            .height(
                200.dp
            ),
        lineChartData = data,
    )
}