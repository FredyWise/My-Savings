package com.fredy.theme.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.exyte.animatednavbar.AnimatedNavigationBar


@Composable
fun AnimatedPointBottomBarTemplate(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedIndex: Int,
    content: @Composable () -> Unit
) {
    AnimatedNavigationBar(
        modifier = modifier
            .selectableGroup()
            .fillMaxWidth(),
        selectedIndex = selectedIndex,
        barColor = backgroundColor,
        ballColor = contentColor,
        content = content
    )
}

@Composable
fun BottomBarItemTemplate(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    description: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    selectedColor: Color,
    unselectedColor: Color,
    label: String,
    animationDuration: Int = 600,
) {
    val color by animateColorAsState(
        targetValue = if (selected) selectedColor else unselectedColor,
        animationSpec = TweenSpec(durationMillis = animationDuration),
        label = "",
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Crossfade(
                targetState = if (selected) selectedIcon else unselectedIcon,
                animationSpec = TweenSpec(
                    durationMillis = animationDuration
                ),
                label = ""
            ) { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    tint = color,
                )
            }
            Text(
                text = label, color = color
            )
        }
    }
}


