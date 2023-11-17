package com.fredy.mysavings.ui.NavigationComponent

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute


@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    allScreens: List<NavigationRoute>,
    onTabSelected: (NavigationRoute) -> Unit,
    currentScreen: NavigationRoute
) {
    BottomNavigation(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = backgroundColor
    ) {
        allScreens.forEach { screen ->
            BottomNavigationItem(
                label = {
                    Text(
                        text = screen.title
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (currentScreen == screen) screen.icon else screen.iconNot,
                        contentDescription = screen.contentDescription
                    )
                },
                selected = currentScreen == screen,
                selectedContentColor = contentColor,
                unselectedContentColor = contentColor.copy(
                    alpha = ContentAlpha.disabled
                ),
                onClick = {
                    onTabSelected(screen)
                },
            )
        }
    }
}
