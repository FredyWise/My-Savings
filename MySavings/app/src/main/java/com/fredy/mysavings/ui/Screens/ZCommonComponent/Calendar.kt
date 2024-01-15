package com.fredy.mysavings.ui.Screens.ZCommonComponent

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil


@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.onSecondary,
    textColor: Color = MaterialTheme.colorScheme.primary,
    calenderRows: Int = 7,
    calenderColumn: Int = 7,
    calendarInput: Map<Int,String>,
    date: LocalDate = LocalDate.now(),
    onDayClick: (Int) -> Unit = {},
    strokeWidth: Float = 5f,
    title: @Composable () -> Unit = {}
) {

    var canvasSize by remember {
        mutableStateOf(Size.Zero)
    }
    var clickAnimationOffset by remember {
        mutableStateOf(Offset.Zero)
    }

    var animationRadius by remember {
        mutableStateOf(0f)
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            10.dp
        )
    ) {
        title()
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(
                true
            ) {
                detectTapGestures(onTap = { offset ->
                    val column = (offset.x / canvasSize.width * calenderColumn).toInt() + 1
                    val row = (offset.y / canvasSize.height * calenderRows).toInt() + 1
                    val day = column + (row - 1) * calenderColumn
                    if (day <= calendarInput.size) {
                        onDayClick(day)
                        clickAnimationOffset = offset
                        scope.launch {
                            animate(
                                0f,
                                225f,
                                animationSpec = tween(
                                    300
                                )
                            ) { value, _ ->
                                animationRadius = value
                            }
                        }
                    }

                })
            }) {
            val canvasHeight = size.height
            val canvasWidth = size.width
            canvasSize = Size(
                canvasWidth, canvasHeight
            )
            val ySteps = canvasHeight / calenderRows
            val xSteps = canvasWidth / calenderColumn

            val column = (clickAnimationOffset.x / canvasSize.width * calenderColumn).toInt() + 1
            val row = (clickAnimationOffset.y / canvasSize.height * calenderRows).toInt() + 1

            val path = Path().apply {
                moveTo(
                    (column - 1) * xSteps,
                    (row - 1) * ySteps
                )
                lineTo(
                    column * xSteps,
                    (row - 1) * ySteps
                )
                lineTo(
                    column * xSteps, row * ySteps
                )
                lineTo(
                    (column - 1) * xSteps,
                    row * ySteps
                )
                close()
            }

            clipPath(path) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(
                            borderColor.copy(
                                0.8f
                            ),
                            borderColor.copy(0.2f)
                        ),
                        center = clickAnimationOffset,
                        radius = animationRadius + 0.1f
                    ),
                    radius = animationRadius + 0.1f,
                    center = clickAnimationOffset
                )
            }

            drawRoundRect(
                color = borderColor,
                cornerRadius = CornerRadius(
                    25f, 25f
                ),
                style = Stroke(
                    width = strokeWidth
                )
            )

            for (i in 1 .. calenderRows) {
                drawLine(
                    color = borderColor,
                    start = Offset(
                        0f, ySteps * i
                    ),
                    end = Offset(
                        canvasWidth, ySteps * i
                    ),
                    strokeWidth = strokeWidth
                )
            }
            for (i in 1 .. calenderColumn) {
                drawLine(
                    color = borderColor,
                    start = Offset(
                        xSteps * i, 0f
                    ),
                    end = Offset(
                        xSteps * i, canvasHeight
                    ),
                    strokeWidth = strokeWidth
                )
            }

            val textHeight = 17.dp.toPx()
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

            for (i in 0 until 7) {
                val textPositionX = xSteps * (i % calenderColumn) + strokeWidth
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        daysOfWeek[i],
                        textPositionX,
                        textHeight,
                        Paint().apply {
                            textSize = textHeight
                            color = textColor.toArgb()
                            typeface = Typeface.DEFAULT_BOLD
                        }
                    )
                }
            }


            for (i in 1.. YearMonth.from(date).lengthOfMonth()) {
                val dayOfWeek = LocalDate.of(date.year, date.month, i).dayOfWeek.value % 7
                val additionalStep = LocalDate.of(date.year, date.month, 1).dayOfWeek.value % 7 - dayOfWeek
                val textPositionX = xSteps * dayOfWeek + strokeWidth
                val textPositionY =
                    ceil((i+ additionalStep + daysOfWeek.indexOf("Sun")) / calenderColumn.toDouble()) * (ySteps) + textHeight + strokeWidth / 2

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        i.toString(),
                        textPositionX,
                        textPositionY.toFloat(),
                        Paint().apply {
                            textSize = textHeight
                            color = textColor.toArgb()
                            isFakeBoldText = true
                        }
                    )
                    drawText(
                        calendarInput[i].orEmpty(),
                        textPositionX,
                        textPositionY.toFloat() + textHeight,
                        Paint().apply {
                            textSize = textHeight - 3.dp.toPx()
                            color = textColor.toArgb()
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }

}