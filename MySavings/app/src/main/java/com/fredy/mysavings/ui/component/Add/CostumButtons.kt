package com.fredy.mysavings.ui.component.Add

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Add.BtnAction
import com.fredy.mysavings.Data.Add.Select
import com.fredy.mysavings.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ButtonWithTitleAndIcon(
    buttonTitle: String = "Title",
    buttonTitleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    buttonTitleColor: Color = MaterialTheme.colorScheme.onBackground,
    buttonIcon: Painter? = null,
    buttonIconDescription: String? = null,
    buttonIconColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonText: String = "RepresentIcon",
    buttonTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    buttonTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonShape: Shape = MaterialTheme.shapes.small,
    buttonBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    borderColor: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buttonTitle,
            style = buttonTitleStyle,
            color = buttonTitleColor,
            modifier = Modifier.padding(3.dp)
        )
        ButtonWithIcon(
            buttonPainter = buttonIcon,
            buttonIconDescription = buttonIconDescription,
            buttonIconColor = buttonIconColor,
            buttonText = buttonText,
            buttonTextStyle = buttonTextStyle,
            buttonTextColor = buttonTextColor,
            buttonShape = buttonShape,
            buttonBackgroundColor = buttonBackgroundColor,
            borderColor = borderColor,
            onClick = onClick,
            modifier = Modifier.padding(vertical = 10.dp)
        )

    }
}

@Composable
fun ConfirmationBar(
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    verticalPadding: Dp = 15.dp,
    modifier: Modifier = Modifier,
    onAction: (BtnAction) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ButtonWithIcon(buttonText = "CANCEL",
            buttonTextColor = textColor,
            buttonIconColor = textColor,
            buttonImageVector = Icons.Default.Close,
            buttonTextStyle = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = verticalPadding
                ),
            onClick = { onAction(BtnAction.Cancel) })
        Spacer(modifier = Modifier.weight(1f))
        ButtonWithIcon(buttonText = "SAVE",
            buttonTextColor = textColor,
            buttonIconColor = textColor,
            buttonImageVector = Icons.Default.Check,
            buttonTextStyle = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = verticalPadding
                ),
            onClick = { onAction(BtnAction.Save) })
    }
}

@Composable
fun ChooseNoteType(
    isSelected: Select,
    verticalPadding: Dp = 15.dp,
    onActionColor: Color = MaterialTheme.colorScheme.onBackground,
    dividerColor: Color = Color.Gray,
    modifier: Modifier = Modifier,
    onAction: (BtnAction) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ButtonWithIcon(buttonImageVector = isSelected.income.icon,
            buttonIconColor = isSelected.income.iconColor,
            buttonTextColor = isSelected.income.textColor,
            buttonText = "INCOME",
            buttonTextStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = verticalPadding
                ),
            onClick = {
                onAction(
                    BtnAction.IncomeSelected(
                        onActionColor
                    )
                )
            })
        Divider(
            modifier = Modifier
                .height(35.dp)
                .width(
                    2.dp
                ), color = dividerColor
        )
        ButtonWithIcon(buttonImageVector = isSelected.expense.icon,
            buttonIconColor = isSelected.expense.iconColor,
            buttonTextColor = isSelected.expense.textColor,
            buttonText = "EXPENSE",
            buttonTextStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = verticalPadding
                ),
            onClick = {
                onAction(
                    BtnAction.ExpenseSelected(
                        onActionColor
                    )
                )
            })
        Divider(
            modifier = Modifier
                .height(35.dp)
                .width(
                    2.dp
                ), color = dividerColor
        )
        ButtonWithIcon(buttonImageVector = isSelected.transfer.icon,
            buttonIconColor = isSelected.transfer.iconColor,
            buttonTextColor = isSelected.transfer.textColor,
            buttonText = "TRANSFER",
            buttonTextStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = verticalPadding
                ),
            onClick = {
                onAction(
                    BtnAction.TransferSelected(
                        onActionColor
                    )
                )
            })
    }
}

@Composable
fun ChooseWalletAndTag(
    spacing: Dp,
    modifier: Modifier = Modifier,
    titleBtn1: String = "Account",
    iconBtn1: Painter = painterResource(
        id = R.drawable.ic_bank_card
    ),
    textBtn1: String = "Account",
    titleBtn2: String = "Category",
    iconBtn2: Painter = painterResource(
        id = R.drawable.ic_tag
    ),
    textBtn2: String = "Category",
    onClickBtn1: () -> Unit,
    onClickBtn2: () -> Unit,
) {
    Row(
        modifier = modifier.padding(bottom = spacing),
        horizontalArrangement = Arrangement.spacedBy(
            spacing
        )
    ) {
        ButtonWithTitleAndIcon(
            buttonTitle = titleBtn1,
            buttonIcon = iconBtn1,
            buttonText = textBtn1,
            modifier = Modifier.weight(1f),
            onClick = onClickBtn1
        )
        ButtonWithTitleAndIcon(
            buttonTitle = titleBtn2,
            buttonIcon = iconBtn2,
            buttonText = textBtn2,
            modifier = Modifier.weight(1f),
            onClick = onClickBtn2
        )
    }
}

@Composable
fun DateAndTimePicker(
    applicationContext: Context,
    verticalPadding: Dp = 15.dp,
    dividerColor: Color = Color.Gray,
    spacing: Dp,
    modifier: Modifier = Modifier,
    dateDialogState: MaterialDialogState,
    timeDialogState: MaterialDialogState,
    formattedDate: String,
    formattedTime: String,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(top = spacing)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ButtonWithIcon(
            onClick = {
                dateDialogState.show()
            },
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = verticalPadding
                ),
            buttonText = formattedDate,
            buttonTextColor = MaterialTheme.colorScheme.onBackground
        )
        Divider(
            modifier = Modifier
                .height(35.dp)
                .width(
                    2.dp
                ), color = dividerColor
        )
        ButtonWithIcon(
            onClick = {
                timeDialogState.show()
            },
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = verticalPadding
                ),
            buttonText = formattedTime,
            buttonTextColor = MaterialTheme.colorScheme.onBackground
        )
    }
    MaterialDialog(dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                Toast.makeText(
                    applicationContext,
                    "Clicked ok",
                    Toast.LENGTH_LONG
                ).show()
            }
            negativeButton(text = "Cancel")
        }) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date",
            onDateChange = onDateChange
        )
    }
    MaterialDialog(dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                Toast.makeText(
                    applicationContext,
                    "Clicked ok",
                    Toast.LENGTH_LONG
                ).show()
            }
            negativeButton(text = "Cancel")
        }) {
        timepicker(
            initialTime = LocalTime.now(),
            title = "Pick a time",
            onTimeChange = onTimeChange
        )
    }
}

@Composable
fun ButtonWithIcon(
    buttonImageVector: ImageVector? = null,
    buttonPainter: Painter? = null,
    buttonIconDescription: String? = null,
    buttonIconColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonText: String = "RepresentIcon",
    buttonTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    buttonTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonShape: Shape = MaterialTheme.shapes.small,
    buttonBackgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    spacing: Dp = 8.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                buttonBackgroundColor, buttonShape
            )
            .clickable {
                onClick()
            }
            .then(modifier)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                spacing
            ),
            modifier = Modifier.border(
                2.dp, borderColor, buttonShape
            )
        ) {
            if (buttonImageVector != null && buttonPainter == null) {
                Icon(
                    imageVector = buttonImageVector,
                    contentDescription = buttonIconDescription,
                    tint = buttonIconColor,
                )
            } else if (buttonPainter != null) {
                Icon(
                    painter = buttonPainter,
                    contentDescription = buttonIconDescription,
                    tint = buttonIconColor,
                    modifier = Modifier.size(
                        width = 40.dp,
                        height = 40.dp
                    )
                )
            }
            Text(
                text = buttonText,
                style = buttonTextStyle,
                color = buttonTextColor,
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Preview(
    showBackground = true, // Show a background
    widthDp = 360, // Set the width to the real screen width
    heightDp = 640 // Set the height to the real screen height
)
@Composable
fun ButtonWithTitlePreview() {
    val screen = LocalDensity.current.run {
        LocalConfiguration.current.screenHeightDp.dp to LocalConfiguration.current.screenWidthDp.dp
    }

    // Create a background with a color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Blue
            ) // Set the background color
    ) {
        Row {
            ButtonWithTitleAndIcon(buttonTitle = "Sample Title",
                buttonText = "Click Me",
                modifier = Modifier.padding(16.dp),
                onClick = {})
            ButtonWithTitleAndIcon(buttonTitle = "Sample Title",
                buttonText = "Click Me",
                modifier = Modifier.padding(16.dp),
                onClick = {})
        }
    }
}

