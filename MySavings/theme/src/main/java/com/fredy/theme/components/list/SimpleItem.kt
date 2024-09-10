package com.fredy.theme.components.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fredy.theme.model.ActionWithName

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
