package com.fredy.mysavings.ui.Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fredy.mysavings.ui.MainScreen
import com.fredy.mysavings.ui.Screens.AddRecord.AddScreen

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
                rootNavController = navController,
            )
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

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(
    route
) { launchSingleTop = true }