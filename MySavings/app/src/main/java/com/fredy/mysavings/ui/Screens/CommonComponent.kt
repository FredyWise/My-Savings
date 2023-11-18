package com.fredy.mysavings.ui.Screens

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.BalanceColor
import com.fredy.mysavings.Data.SavingsIcon
import com.fredy.mysavings.Data.formatBalanceAmount

@Composable
fun SimpleAddDialog(
    modifier: Modifier = Modifier,
    title: String,
    onDismissRequest: () -> Unit,
    onCancelClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(modifier = modifier,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    8.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            Box(modifier = modifier
                .clip(
                    MaterialTheme.shapes.small
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.small
                )
                .clickable {
                    onCancelClicked()
                }) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = "CANCEL"
                )
            }
        },
        confirmButton = {
            Box(modifier = modifier
                .clip(
                    MaterialTheme.shapes.small
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.small
                )
                .clickable {
                    onSaveClicked()
                }) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = "SAVE"
                )
            }
        })
}

@Composable
fun AdvancedEntityItem(
    modifier: Modifier = Modifier,
    icon: Int,
    iconModifier: Modifier = Modifier,
    iconDescription: String,
    menuItems: List<ActionWithName>,
    content: @Composable () -> Unit,
) {
    var isShowMenu by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    SimpleEntityItem(modifier = modifier
        .indication(
            interactionSource,
            LocalIndication.current
        )
        .pointerInput(true) {
            detectTapGestures(onLongPress = {
                isShowMenu = true
            }, onPress = {
                val press = PressInteraction.Press(
                    it
                )
                interactionSource.emit(
                    press
                )
                tryAwaitRelease()
                interactionSource.emit(
                    PressInteraction.Release(
                        press
                    )
                )
            })
        }
        .padding(
            8.dp
        ),
        icon = icon,
        iconModifier = iconModifier,
        iconDescription = iconDescription,
        content = content,
        endContent = {
            Icon(modifier = Modifier
                .clip(
                    MaterialTheme.shapes.large
                )
                .clickable {
                    isShowMenu = true
                }
                .padding(4.dp),
                imageVector = Icons.Default.MoreVert,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,)
            CustomDropDownMenu(pressOffset = pressOffset,
                menuItems = menuItems,
                isShowMenu = isShowMenu,
                onClose = { isShowMenu = false })
        },)
}


@Composable
fun SimpleEntityItem(
    modifier: Modifier = Modifier,
    icon: Int,
    iconModifier: Modifier = Modifier,
    iconDescription: String,
    contentWeight: Float = 1f,
    endContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = iconModifier,
            painter = painterResource(
                icon
            ),
            contentDescription = iconDescription,
            tint = Color.Unspecified
        )
        Column(
            modifier = Modifier
                .weight(
                    contentWeight
                )
                .padding(
                    horizontal = 8.dp
                ),
            horizontalAlignment = Alignment.Start,
        ) {
            content()
        }
        endContent()
    }
}

@Composable
fun CustomStickyHeader(
    modifier: Modifier = Modifier,
    title: String,
    textStyle: TextStyle
) {
    Column(
        modifier = modifier.background(
            MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = title,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 28.dp,
                    start = 8.dp,
                    bottom = 4.dp
                ),
        )
        Divider(
            modifier = Modifier
                .padding(start = 4.dp)
                .height(
                    2.dp
                ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TypeRadioButton(
    modifier: Modifier = Modifier,
    selectedName: String,
    radioButtons: List<ActionWithName>,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    barHeight: Dp = 35.dp,
    ) {
    Row(
        modifier = modifier.fillMaxWidth().height(barHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        radioButtons.forEachIndexed { index, button ->
            if (index > 0 && index < radioButtons.size) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(
                            2.dp
                        ),
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.5f
                    )
                )
            }
            Row(modifier = Modifier
                .clickable {
                    button.action()
                }
                .padding(vertical = 8.dp)
                .weight(
                    1f
                ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                if (selectedName == button.name) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = button.name,
                    style = textStyle,
                    color = if (selectedName == button.name) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.5f
                    )
                )
            }
        }
    }
}

@Composable
fun ChooseIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    onClick: (SavingsIcon) -> Unit,
    selectedIcon: Int,
    icons: List<SavingsIcon>,
) {
    var selectedIcon by remember {
        mutableIntStateOf(
            selectedIcon
        )
    }
    LazyRow(//change to lazy horizontal grid
        modifier = modifier.background(
            MaterialTheme.colorScheme.background
        )
    ) {
        items(icons) { icon ->
            Box(modifier = Modifier
                .clickable {
                    onClick(icon)
                    selectedIcon = icon.image
                }
                .clip(
                    shape = MaterialTheme.shapes.medium
                )
                .background(
                    color = if (selectedIcon == icon.image) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background
                )
                .padding(8.dp)) {
                Icon(
                    modifier = iconModifier
                        .width(
                            50.dp
                        )
                        .height(50.dp),
                    painter = painterResource(
                        icon.image
                    ),
                    contentDescription = icon.description,
                    tint = Color.Unspecified
                )
            }
        }
    }
}


@Composable
fun SimpleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    image: Int? = null,
    imageColor: Color = Color.Unspecified,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Box(
        modifier = modifier.clickable {
            onClick()
        }, contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(
                8.dp
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            image?.let {
                Icon(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(id = it),
                    contentDescription = "",
                    tint = imageColor,
                )
            }
            Text(
                text = title, style = titleStyle
            )
        }
    }
}

@Composable
fun CustomDropDownMenu(
    pressOffset: DpOffset,
    menuItems: List<ActionWithName>,
    isShowMenu: Boolean = false,
    onClose: () -> Unit,
) {
    DropdownMenu(
        expanded = isShowMenu,
        onDismissRequest = {
            onClose()
        },
        offset = pressOffset.copy(
            y = pressOffset.y - 10.dp,
            x = pressOffset.x + 400.dp
        )
    ) {
        menuItems.forEach { item ->
            DropdownMenuItem(modifier = Modifier
                .fillMaxSize()
                .padding(
                    end = 8.dp
                ), onClick = {
                item.action()
                onClose()
            }, text = {
                Text(
                    text = item.name
                )
            })
        }
    }
}

data class ActionWithName(
    val name: String, val action: () -> Unit
)

@Composable
fun BalanceItem(
    modifier: Modifier = Modifier,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    amount: Double,
    amountColor: Color = BalanceColor(
        amount = amount,
    ),
    currency: String,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    titleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    amountStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
    ) {
        Text(
            text = title,
            color = titleColor,
            style = titleStyle
        )
        Text(
            text = formatBalanceAmount(
                amount = amount,
                currency = currency
            ),
            color = amountColor,
            style = amountStyle
        )
    }
}