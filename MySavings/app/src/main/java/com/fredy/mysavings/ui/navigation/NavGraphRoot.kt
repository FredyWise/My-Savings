package com.fredy.mysavings.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.mysavings.MySavingsApp
import com.fredy.mysavings.ui.screens.AddScreen
import com.fredy.mysavings.ui.screens.MainScreen

@Composable
fun NavGraphRoot(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Graph.BottomNav,
        route = Graph.Root
    ) {
        authenticationNavGraph(navController = navController)
        composable(
            route = Graph.BottomNav
        ) {
            MainScreen(
                rootNavController = navController
            )
        }
        composable(
            route = NavigationRoute.Add.route
        ) {
            AddScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(
    route
) { launchSingleTop = true }