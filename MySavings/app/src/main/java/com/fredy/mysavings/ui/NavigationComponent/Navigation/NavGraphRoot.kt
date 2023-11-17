package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fredy.mysavings.ui.Screens.AddRecord.AddScreen
import com.fredy.mysavings.ui.Screens.MainScreen

@Composable
fun NavGraphRoot(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Graph.MainNav,
        route = Graph.Root
    ) {
        authenticationNavGraph(navController = navController)
        navigation(
            route = Graph.MainNav,
            startDestination = Graph.HomeNav,
        ) {
            composable(
                route = Graph.HomeNav
            ) {
                MainScreen(
                    rootNavController = navController,
                )
            }
            composable(
                route = NavigationRoute.Preferences.route
            ) {
//            PreferencesScreen()
            }
            composable(
                route = NavigationRoute.Export.route
            ) {
//            ExportScreen()
            }
            composable(
                route = NavigationRoute.Restore.route
            ) {
//            RestoreScreen()
            }
            composable(
                route = NavigationRoute.Reset.route
            ) {
//            ResetScreen()
            }
            composable(
                route = "${NavigationRoute.Add.route}?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val id = it.arguments?.getInt("id") ?: -1
                AddScreen(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.background
                    )
                    .padding(8.dp),
                    id = id,
                    navigateUp = { navController.navigateUp() })
            }
        }

    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(
    route
) {
    popUpTo(
        this@navigateSingleTopTo.graph.findStartDestination().id
    ) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}