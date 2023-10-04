package com.fredy.mysavings.ui.bottomBar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.ui.navigation.NavigationRoute


@Composable
fun BottomBar(
    allScreens: List<NavigationRoute>,
    onTabSelected: (NavigationRoute) -> Unit,
    currentScreen: NavigationRoute
) {
    Surface(
        Modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            allScreens.forEach { screen ->
                BottomNavigationItem(label = {
                    Text(
                        text = screen.title
                    )
                },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == screen) screen.icon else screen.iconNot,
                            contentDescription = "Navigation Icon"
                        )
                    },
                    selected = currentScreen == screen,
                    selectedContentColor = MaterialTheme.colorScheme.onSurface,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = ContentAlpha.disabled
                    ),
                    onClick = {
                        onTabSelected(screen)
                    })
            }
        }
    }
}
