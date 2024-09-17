package com.fredy.ui.components.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fredy.domain.modelUI.ActionWithName


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