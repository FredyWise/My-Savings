package com.fredy.mysavings.ui.Screens.Analysis

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import java.util.Locale

@Composable
fun AnalysisTabRow(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    allScreens: List<NavigationRoute>,
    onTabSelected: (screen: NavigationRoute) -> Unit,
    currentScreen: NavigationRoute
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, color, CircleShape)
            .height(IntrinsicSize.Max)
            .selectableGroup(),
        horizontalArrangement = Arrangement.Center,
    ) {
        allScreens.forEach { screen ->
            AnalysisTab(
                textColor = color,
                text = screen.title,
                icon = screen.icon,
                onSelected = { onTabSelected(screen) },
                selected = currentScreen == screen
            )
        }
    }

}

@Composable
private fun AnalysisTab(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    text: String,
    icon: ImageVector,
    onSelected: () -> Unit,
    selected: Boolean
) {
    val durationMillis = if (selected) 200 else 150
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = 150
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (selected) textColor else textColor.copy(alpha = 0.60f),
        animationSpec = animSpec, label = ""
    )
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .animateContentSize()
            .height(IntrinsicSize.Max)
            .clip(CircleShape)
            .padding(3.dp)
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics { contentDescription = text }
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = tabTintColor)
        if (selected) {
            Spacer(Modifier.width(12.dp))
            Text(text = text.uppercase(Locale.getDefault()), color = tabTintColor)
        }
    }
}
