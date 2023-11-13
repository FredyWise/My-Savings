package com.fredy.mysavings.ui.Navigation

import androidx.compose.runtime.Composable
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
//                onNavigate = { id ->
//                    navController.navigate(route = "${NavigationRoute.Add.route}?id=$id")
//                }
            )
        }
        composable(
            route = "${NavigationRoute.Add.route}",//?id={id}
            //arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val id = -1//it.arguments?.getInt("id") ?: -1
            AddScreen(
                id = id,
                navigateUp = { navController.navigateUp() }
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(
    route
) { launchSingleTop = true }