package com.fredy.mysavings.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.ui.bottomBar.BottomBar
import com.fredy.mysavings.ui.navigation.BottomNavGraph
import com.fredy.mysavings.ui.navigation.NavigationRoute
import com.fredy.mysavings.ui.navigation.bottomBarScreens
import com.fredy.mysavings.ui.navigation.navigateSingleTopTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = bottomBarScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records
    Scaffold(bottomBar = {
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
                    NavigationRoute.Add.route
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