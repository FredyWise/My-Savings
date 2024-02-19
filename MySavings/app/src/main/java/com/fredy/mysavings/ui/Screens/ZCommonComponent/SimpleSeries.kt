package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.Util.savingsIcons

@Composable
fun SimpleWarningDialog(
    isShowWarning: Boolean = false,
    onDismissRequest: () -> Unit,
    onSaveClicked: () -> Unit,
    warningText: String
) {
    if (isShowWarning) {
        SimpleDialog(
            title = "Warning!!",
            cancelName = "No",
            saveName = "Yes",
            onDismissRequest = {
                onDismissRequest()
            },
            onSaveClicked = {
                onSaveClicked()
                onDismissRequest()
            },
        ) {
            Text(
                text = warningText,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun SimpleAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    onDismissRequest: () -> Unit,
    leftButton: @Composable (() -> Unit)? = null,
    rightButton: @Composable () -> Unit = { },
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
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
        dismissButton = leftButton,
        confirmButton = rightButton,
    )
}

@Composable
fun SimpleDialog(
    modifier: Modifier = Modifier,
    dismissOnSave: Boolean = true,
    title: String,
    cancelName: String = "Cancel",
    saveName: String = "Save",
    onDismissRequest: () -> Unit,
    onSaveClicked: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
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
        confirmButton = {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    modifier = Modifier
                        .weight(0.4f)
                        .clip(
                            MaterialTheme.shapes.small
//                        )
//                        .border(
//                            width = 2.dp,
//                            color = MaterialTheme.colorScheme.secondary,
//                            shape = MaterialTheme.shapes.small
                        ),
                    onClick = onDismissRequest,
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = cancelName
                    )
                }
                Spacer(
                    modifier = Modifier.weight(
                        0.05f
                    )
                )
                Button(
                    modifier = Modifier
                        .weight(0.4f)
                        .clip(
                            MaterialTheme.shapes.small
//                        )
//                        .border(
//                            width = 2.dp,
//                            color = MaterialTheme.colorScheme.secondary,
//                            shape = MaterialTheme.shapes.small
                        ),
                    onClick = {
                        onSaveClicked()
                        if (dismissOnSave) {
                            onDismissRequest()
                        }
                    },
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = saveName
                    )
                }
            }
        },
    )
}


@Composable
fun SimpleEntityItem(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    icon: Int? = null,
    iconModifier: Modifier = Modifier,
    iconDescription: String,
    iconColor: Color = Color.Unspecified,
    contentWeight: Float = 1f,
    endContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                modifier = iconModifier,
                painter = painterResource(
                    id = savingsIcons[iconDescription]?.image ?: icon
                ),
                contentDescription = iconDescription,
                tint = iconColor
            )
        }
        Column(
            modifier = if (contentWeight != 0f) {
                contentModifier.weight(contentWeight)
            } else {
                contentModifier
            }.padding(
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
fun SimpleItem(
    modifier: Modifier = Modifier,
    menuItems: List<ActionWithName> = emptyList(),
    onClick: () -> Unit = {},
    contentWeight: Float = 1f,
    endContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    var isShowMenu by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    Row(
        modifier = modifier
            .clip(
                CircleShape
            )
            .clickable {
                onClick()
                if (menuItems.isNotEmpty()) {
                    isShowMenu = true
                }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
        if (menuItems.isNotEmpty()) {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
            SimpleDropDownMenu(pressOffset = pressOffset,
                menuItems = menuItems,
                isShowMenu = isShowMenu,
                onClose = { isShowMenu = false })
        }
    }
}


@Composable
fun SimpleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    image: Int? = null,
    imageDescription: String = "",
    imageColor: Color = Color.Unspecified,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        SimpleEntityItem(
            modifier = Modifier.padding(
                8.dp
            ),
            iconModifier = Modifier
                .size(40.dp)
                .padding(end = 4.dp),
            icon = image,
            iconDescription = imageDescription,
            iconColor = imageColor,
            contentWeight = 0f
        ) {
            Text(
                text = title,
                style = titleStyle,
                color = titleColor,
                maxLines = 1
            )
        }
    }
}


@Composable
fun SimpleDropDownMenu(
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