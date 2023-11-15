package com.fredy.mysavings.ui.Navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.mysavings.ui.Screens.Account.AccountsScreen
import com.fredy.mysavings.ui.Screens.Category.CategoriesScreen
import com.fredy.mysavings.ui.Screens.Record.RecordsScreen


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
            RecordsScreen(rootNavController = navController)
        }
        composable(
            route = NavigationRoute.Analysis.route
        ) {
//            AnalysisScreen()
        }
        composable(
            route = NavigationRoute.Categories.route
        ) {
            CategoriesScreen(modifier = Modifier.padding(8.dp))
        }
        composable(
            route = NavigationRoute.Account.route
        ) {
            AccountsScreen(modifier = Modifier.padding(8.dp))
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