package com.fredy.ui.components.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.domain.modelUI.ActionWithName


@Composable
fun TypeRadioButton(
    modifier: Modifier = Modifier,
    onSelectedColor: Color = MaterialTheme.colorScheme.onBackground,
    selectedName: String,
    radioButtons: List<ActionWithName>,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    barHeight: Dp = 35.dp,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup()
            .height(
                barHeight
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        radioButtons.forEachIndexed { index, button ->
            val durationMillis = if (selectedName == button.name) 100 else 50
            val animSpec = remember {
                tween<Color>(
                    durationMillis = durationMillis,
                    easing = LinearEasing,
                    delayMillis = 100
                )
            }
            val selectedColor by animateColorAsState(
                targetValue = if (selectedName == button.name) onSelectedColor else onSelectedColor.copy(
                    alpha = 0.5f
                ),
                animationSpec = animSpec, label = ""
            )
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
            Row(
                modifier = Modifier
                    .animateContentSize()
                    .selectable(
                        selected = selectedName == button.name,
                        onClick = button.action,
                        role = Role.RadioButton,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = Dp.Unspecified,
                            color = Color.Unspecified
                        )
                    )
                    .padding(vertical = 8.dp)
                    .weight(
                        1f
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedName == button.name) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "",
                        tint = selectedColor
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = button.name,
                    style = textStyle,
                    color = selectedColor
                )
            }
        }
    }
}

