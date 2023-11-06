package com.fredy.mysavings.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.mysavings.ui.screens.AccountsScreen
import com.fredy.mysavings.ui.screens.AddScreen
import com.fredy.mysavings.ui.screens.AnalysisScreen
import com.fredy.mysavings.ui.screens.CategoriesScreen
import com.fredy.mysavings.ui.screens.RecordsScreen


@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Records.route,
        modifier = modifier
    ) {
        composable(
            route = NavigationRoute.Records.route
        ) {
            RecordsScreen()
        }
        composable(
            route = NavigationRoute.Analysis.route
        ) {
            AnalysisScreen()
        }
        composable(
            route = NavigationRoute.Categories.route
        ) {
            CategoriesScreen()
        }
        composable(
            route = NavigationRoute.Account.route
        ) {
            AccountsScreen()
        }

//        composable(
//            route = BottomBarRoute.Profile.route,
//            arguments = listOf(
//                navArgument(DETAIL_ARGUMENT_KEY) {
//                    type = NavType.IntType
//                    defaultValue = 0
//                },
//                navArgument(DETAIL_ARGUMENT_KEY2) {
//                    type = NavType.StringType
//                    defaultValue = "-"
//                }
//            )
//        ) {
//            ProfileScreen(navController = navController)
//        }
    }
}



//private fun NavHostController.navigateToSingleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}