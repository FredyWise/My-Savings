package com.fredy.mysavings.Feature.Presentation.Screens.NavigationComponent

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.utils.noRippleClickable
import com.fredy.mysavings.Feature.Presentation.Navigation.NavigationRoute


@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    allScreens: List<NavigationRoute>,
    onTabSelected: (NavigationRoute) -> Unit,
    currentScreen: NavigationRoute
) {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    AnimatedNavigationBar(
        modifier = modifier
            .selectableGroup()
            .fillMaxWidth(),
        selectedIndex = selectedIndex,
        barColor = backgroundColor,
        ballColor = contentColor
    ) {
        allScreens.forEachIndexed { index, screen ->
            BottomBarItem(
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = {
                    selectedIndex = index
                    onTabSelected(screen)
                },
                selected = currentScreen == screen,
                selectedIcon = screen.icon,
                unselectedIcon = screen.iconNot,
                description = screen.contentDescription,
                selectedColor = contentColor,
                unselectedColor = contentColor.copy(
                    alpha = ContentAlpha.disabled
                ),
                label = screen.title
            )
        }
    }
}

@Composable
fun BottomBarItem(
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
    animationSpec = TweenSpec(durationMillis = animationDuration), label = "",
)
    Box(
        modifier = modifier
            .fillMaxSize()
            .noRippleClickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Crossfade(targetState = if (selected) selectedIcon else unselectedIcon,
                animationSpec = TweenSpec(durationMillis = animationDuration),
                label = ""
            ) { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    tint = color,
                )
            }
            Text(
                text = label,
                color = color
            )
        }
    }
}


