package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun SimpleAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    onDismissRequest: () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit = { },
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
        dismissButton = dismissButton,
        confirmButton = confirmButton,
    )
}

@Composable
fun SimpleDialog(
    modifier: Modifier = Modifier,
    title: String,
    onDismissRequest: () -> Unit,
    onCancelClicked: () -> Unit,
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
        },
    )
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
fun SimpleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    image: Int? = null,
    imageColor: Color = Color.Unspecified,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    titleColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable {
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
                text = title,
                style = titleStyle,
                color = titleColor
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