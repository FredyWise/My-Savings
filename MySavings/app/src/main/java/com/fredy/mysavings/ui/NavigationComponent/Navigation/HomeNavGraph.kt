package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fredy.mysavings.ui.Screens.Account.AccountsScreen
import com.fredy.mysavings.ui.Screens.AddRecord.AddScreen
import com.fredy.mysavings.ui.Screens.Category.CategoriesScreen
import com.fredy.mysavings.ui.Screens.Record.RecordsScreen


@Composable
fun HomeNavGraph(
    rootNavController: NavHostController,
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
            RecordsScreen(rootNavController = rootNavController)
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