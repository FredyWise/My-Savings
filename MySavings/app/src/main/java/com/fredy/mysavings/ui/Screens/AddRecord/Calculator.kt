package com.fredy.mysavings.ui.Screens.AddRecord

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.RoomDatabase.Event.CalcEvent
import com.fredy.mysavings.Data.RoomDatabase.Event.CalcOperation
import com.fredy.mysavings.ViewModel.CalcState

@Composable
fun Calculator(
    state: CalcState,
    modifier: Modifier = Modifier,
    btnSpacing: Dp = 10.dp,
    onAction: (CalcEvent) -> Unit,
    calculatorShape: Shape = MaterialTheme.shapes.small,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
    textBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    buttonTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    numberButtonBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    operandButtonBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = Color.Transparent,
    buttonAspectRatio: Float = 1f

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
            Text(
                text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .background(
                        textBackgroundColor,
                        calculatorShape
                    )
                    .border(1.dp, color = borderColor, calculatorShape)
                    .fillMaxWidth()
                    .padding(
                        vertical = 5.dp
                    )
                    .padding(horizontal = 25.dp),
                style = MaterialTheme.typography.displayLarge,
                color = textColor,
                maxLines = 2
            )
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
                        .border(2.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(CalcEvent.Clear)
                    })
                CalcButton(calcSymbol = "%",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(CalcEvent.Percent)
                    })
                CalcButton(calcSymbol = "Del",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(CalcEvent.Delete)
                    })
                CalcButton(calcSymbol = "/",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Operation(
                                CalcOperation.Divide
                            )
                        )
                    })
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
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "7"
                            )
                        )
                    })
                CalcButton(calcSymbol = "8",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "8"
                            )
                        )
                    })
                CalcButton(calcSymbol = "9",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "9"
                            )
                        )
                    })
                CalcButton(calcSymbol = "x",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Operation(
                                CalcOperation.Multiply
                            )
                        )
                    })
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
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "4"
                            )
                        )
                    })
                CalcButton(calcSymbol = "5",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "5"
                            )
                        )
                    })
                CalcButton(calcSymbol = "6",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "6"
                            )
                        )
                    })
                CalcButton(calcSymbol = "-",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Operation(
                                CalcOperation.Substract
                            )
                        )
                    })
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
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "1"
                            )
                        )
                    })
                CalcButton(calcSymbol = "2",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "2"
                            )
                        )
                    })
                CalcButton(calcSymbol = "3",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "3"
                            )
                        )
                    })
                CalcButton(calcSymbol = "+",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Operation(
                                CalcOperation.Add
                            )
                        )
                    })
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
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "0"
                            )
                        )
                    })
                CalcButton(calcSymbol = "00",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(
                            CalcEvent.Number(
                                "00"
                            )
                        )
                    })
                CalcButton(calcSymbol = ".",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            numberButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(CalcEvent.DecimalPoint)
                    })
                CalcButton(calcSymbol = "=",
                    textColor = buttonTextColor,
                    modifier = Modifier
                        .background(
                            operandButtonBackgroundColor,
                            calculatorShape
                        )
                        .border(1.dp, color = borderColor, calculatorShape)
                        .aspectRatio(
                            buttonAspectRatio
                        )
                        .weight(1f),
                    onClick = {
                        onAction(CalcEvent.Calculate)
                    })
            }
        }
    }
}


@Composable
fun CalcButton(
    calcSymbol: String,
    modifier: Modifier,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickable {
                onClick()
            }
            .then(modifier)) {
        Text(
            text = calcSymbol,
            style = MaterialTheme.typography.headlineSmall,
            color = textColor
        )
    }
}



