package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddSingle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddSingle.CalcState
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddSingle.CalcEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddSingle.CalcOperation

@Composable
fun Calculator(
    state: CalcState,
    modifier: Modifier = Modifier,
    btnSpacing: Dp = 10.dp,
    onAction: (CalcEvent) -> Unit,
    leadingObject: @Composable () -> Unit = {},
    calculatorShape: Shape = MaterialTheme.shapes.medium,
    textStyle: TextStyle = MaterialTheme.typography.displaySmall,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
    textBackgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
    buttonTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    numberButtonBackgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
    operandButtonBackgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.4f),
    borderColor: Color = Color.Transparent,
    buttonAspectRatio: Float = 1f,

    ) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(
                    Alignment.BottomCenter
                ),
            verticalArrangement = Arrangement.spacedBy(
                btnSpacing
            ),

            ) {
            Row(
                modifier = Modifier
                    .background(
                        textBackgroundColor,
                        calculatorShape
                    )
                    .border(
                        1.dp,
                        color = borderColor,
                        calculatorShape
                    )
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingObject()
                Text(
                    modifier = Modifier
                        .weight(0.75f)
                        .padding(end = 20.dp, start = 8.dp),
                    text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                    textAlign = TextAlign.End,
                    style = textStyle,
                    color = textColor,
                    maxLines = 1
                )
            }
            // First row (AC - Del - /)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    btnSpacing
                )
            ) {
                CalcButton(calcSymbol = "AC",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Clear)
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "%",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Percent)
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "Del",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Delete)
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "/",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Operation(CalcOperation.Divide))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
            }
            // Second row (7, 8, 9, x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    btnSpacing
                )
            ) {
                CalcButton(calcSymbol = "7",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("7"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "8",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("8"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "9",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("9"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "x",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Operation(CalcOperation.Multiply))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
            }
            // Third row (4, 5, 6, -)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    btnSpacing
                )
            ) {
                CalcButton(calcSymbol = "4",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("4"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "5",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("5"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "6",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("6"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "-",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Operation(CalcOperation.Substract))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
            }
            // Fourth row (1, 2, 3, +)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    btnSpacing
                )
            ) {
                CalcButton(calcSymbol = "1",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("1"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "2",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("2"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "3",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("3"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "+",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Operation(CalcOperation.Add))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
            }
            // Fifth row (0, decimal, equals)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    btnSpacing
                )
            ) {
                CalcButton(calcSymbol = "0",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("0"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "00",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Number("00"))
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = ".",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.DecimalPoint)
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
                CalcButton(calcSymbol = "=",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .clip(calculatorShape)
                        .border(
                            1.dp,
                            color = borderColor,
                            calculatorShape
                        )
                        .clickable {
                            onAction(CalcEvent.Calculate)
                        }
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f))
            }
        }
    }
}


@Composable
fun CalcButton(
    calcSymbol: String,
    modifier: Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = calcSymbol,
            style = MaterialTheme.typography.headlineSmall,
            color = textColor
        )
    }
}



