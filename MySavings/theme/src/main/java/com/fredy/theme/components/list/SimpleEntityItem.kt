package com.fredy.theme.components.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.SavingsIcons.savingsIcons

@Composable
fun SimpleEntityItem(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    icon: Int? = null,
    iconModifier: Modifier = Modifier,
    iconDescription: String = "",
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