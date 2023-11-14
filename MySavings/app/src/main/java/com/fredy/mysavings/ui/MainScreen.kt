package com.fredy.mysavings.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.ui.BottomBar.BottomBar
import com.fredy.mysavings.ui.Navigation.BottomNavGraph
import com.fredy.mysavings.ui.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Navigation.bottomBarScreens
import com.fredy.mysavings.ui.Navigation.navigateSingleTopTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = bottomBarScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records
    Scaffold(modifier = modifier,bottomBar = {
        BottomBar(
            allScreens = bottomBarScreens,
            onTabSelected = { newScreen ->
                navController.navigateSingleTopTo(
                    newScreen.route
                )
            },
            currentScreen = currentScreen
        )
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { rootNavController.navigateSingleTopTo(
                    NavigationRoute.Add.route+"?id=-1"
                )},
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(
                    NavigationRoute.Add.icon,
                    modifier = Modifier.size(30.dp),
                    contentDescription = null
                )
            }
        }

    ) { innerPadding ->
        BottomNavGraph(
            navController = navController,
            modifier = Modifier.padding(
                innerPadding
            ),
        )
    }
}