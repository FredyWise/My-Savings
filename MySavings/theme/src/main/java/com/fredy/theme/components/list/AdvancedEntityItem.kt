package com.fredy.theme.components.list

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fredy.theme.model.ActionWithName

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
    SimpleEntityItem(
        modifier = modifier
            .indication(
                interactionSource,
                LocalIndication.current
            )
//            .pointerInput(true) {
//                detectTapGestures(onLongPress = {
//                    isShowMenu = true
//                }, onPress = {
//                    val press = PressInteraction.Press(
//                        it
//                    )
//                    interactionSource.emit(
//                        press
//                    )
//                    tryAwaitRelease()
//                    interactionSource.emit(
//                        PressInteraction.Release(
//                            press
//                        )
//                    )
//                })
//            }
            .padding(
                8.dp
            ),
        icon = icon,
        iconModifier = iconModifier,
        iconDescription = iconDescription,
        content = content,
        endContent = {
            Icon(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        isShowMenu = true
                    }
                    .padding(4.dp),
                imageVector = Icons.Default.MoreVert,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
            SimpleDropDownMenu(pressOffset = pressOffset,
                menuItems = menuItems,
                isShowMenu = isShowMenu,
                onClose = { isShowMenu = false })
        },
    )
}
