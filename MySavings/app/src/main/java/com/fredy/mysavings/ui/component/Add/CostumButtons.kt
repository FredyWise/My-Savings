package com.fredy.mysavings.ui.component.Add

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Add.BtnAction
import com.fredy.mysavings.Data.Add.MutableTitle
import com.fredy.mysavings.Data.Add.SelectOperation
import com.fredy.mysavings.Data.Records.Item
import com.fredy.mysavings.Data.User.AccountIcons
import com.fredy.mysavings.Data.User.CategoryIcons
import com.fredy.mysavings.ui.component.BasicButton
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ButtonWithTitleAndIcon(
    buttonTitle: String,
    buttonTitleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    buttonTitleColor: Color = MaterialTheme.colorScheme.onBackground,
    buttonIcon: Painter? = null,
    buttonIconDescription: String? = null,
    buttonIconColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonText: String,
    buttonTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    buttonTextColor: Color = MaterialTheme.colorScheme.onSecondary,
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
    verticalPadding: Dp = 10.dp,
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
    selectOperations: List<SelectOperation>,
    verticalPadding: Dp = 15.dp,
    dividerColor: Color = Color.Gray,
    modifier: Modifier = Modifier,
    onAction: (BtnAction) -> Unit,
) {
    var selectedIndex by remember {
        mutableIntStateOf(
            1
        )
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        selectOperations.forEachIndexed { index, operation ->
            if (index > 0 && index < selectOperations.size) {
                Divider(
                    modifier = Modifier
                        .height(35.dp)
                        .width(
                            2.dp
                        ), color = dividerColor
                )
            }
            ButtonWithIcon(buttonImageVector = operation.selectedState.icon,
                buttonIconColor = Color.Gray,
                buttonTextColor = Color.Gray,
                buttonText = operation.selectedState.text,
                buttonTextStyle = MaterialTheme.typography.titleMedium,
                selected = selectedIndex == index,
                selectedColor = MaterialTheme.colorScheme.onBackground,
                removeIconIfNotSelected = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        vertical = verticalPadding
                    ),
                onClick = {
                    onAction(
                        BtnAction.Operation(
                            operation
                        )
                    )
                    selectedIndex = index

                })
        }

    }
}

@Composable
fun ChooseWalletAndTag(
    spacing: Dp,
    modifier: Modifier = Modifier,
    item: Item,
    btnTitle: MutableTitle,
    onAction: (BtnAction) -> Unit,
) {
    Row(
        modifier = modifier.padding(bottom = spacing),
        horizontalArrangement = Arrangement.spacedBy(
            spacing
        )
    ) {
        ButtonWithTitleAndIcon(buttonTitle = btnTitle.fromTitle,
            buttonIcon = AccountIcons(item.account.icon),
            buttonText = item.account.name,
            modifier = Modifier.weight(1f),
            onClick = {
                onAction(
                    BtnAction.AccountClicked
                )
            })
        item.toCategory?.let {
            ButtonWithTitleAndIcon(buttonTitle = btnTitle.toTitle,
                buttonIcon = CategoryIcons(it.icon),
                buttonText = it.name,
                modifier = Modifier.weight(1f),
                onClick = {
                    onAction(
                        BtnAction.ToCategoryClicked
                    )
                })
        }
        item.toAccount?.let {
            ButtonWithTitleAndIcon(buttonTitle = btnTitle.toTitle,
                buttonIcon = AccountIcons(it.icon),
                buttonText = it.name,
                modifier = Modifier.weight(1f),
                onClick = {
                    onAction(
                        BtnAction.ToAccountClicked
                    )
                })
        }
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
        backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
            onDateChange = onDateChange
        )
    }
    MaterialDialog(dialogState = timeDialogState,
        backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
            onTimeChange = onTimeChange
        )
    }
}


@Composable
fun ButtonWithIcon(
    modifier: Modifier = Modifier,
    buttonImageVector: ImageVector? = null,
    buttonPainter: Painter? = null,
    buttonIconDescription: String? = null,
    buttonText: String = "RepresentIcon",
    buttonTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    selected: Boolean = false,
    removeIconIfNotSelected: Boolean = false,
    selectedColor: Color = Color.Unspecified,
    buttonIconColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonBackgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    spacing: Dp = 8.dp,
    onClick: () -> Unit
) {
    BasicButton(modifier = modifier,
        onClick = onClick,
        spacing = spacing,
        buttonBackgroundColor = buttonBackgroundColor,
        borderColor = borderColor,
        btnElement = {
            if (!(selected == false && removeIconIfNotSelected == true)) {
                if (buttonImageVector != null && buttonPainter == null) {
                    Icon(
                        imageVector = buttonImageVector,
                        contentDescription = buttonIconDescription,
                        tint = if (selected) selectedColor else buttonIconColor,
                    )
                } else if (buttonPainter != null) {
                    Icon(
                        painter = buttonPainter,
                        contentDescription = buttonIconDescription,
                        tint = if (selected) selectedColor else buttonIconColor,
                        modifier = Modifier.size(
                            width = 40.dp,
                            height = 40.dp
                        )
                    )
                }
            }
            Text(
                text = buttonText,
                style = buttonTextStyle,
                color = if (selected) selectedColor else buttonTextColor,
            )
        })
}


