package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fredy.mysavings.ViewModels.AccountViewModel
import com.fredy.mysavings.ViewModels.AnalysisViewModel
import com.fredy.mysavings.ViewModels.CategoryViewModel
import com.fredy.mysavings.ViewModels.RecordViewModel
import com.fredy.mysavings.ui.Screens.Account.AccountsScreen
import com.fredy.mysavings.ui.Screens.Analysis.AnalysisScreen
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
            val viewModel: RecordViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val resource by viewModel.resource

            RecordsScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = viewModel::onEvent,
                resource = resource,
            )
        }
        composable(
            route = NavigationRoute.Analysis.route
        ) {
            val viewModel: AnalysisViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val resource by viewModel.resource

            AnalysisScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = viewModel::onEvent,
                resource = resource,
            )
        }
        composable(
            route = NavigationRoute.Categories.route
        ) {
            val viewModel: CategoryViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            CategoriesScreen(
                modifier = Modifier.padding(8.dp),
                state = state,
                onEvent = viewModel::onEvent
            )
        }
        composable(
            route = NavigationRoute.Account.route
        ) {
            val viewModel: AccountViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            AccountsScreen(
                modifier = Modifier.padding(
                    8.dp
                ),
                state = state,
                onEvent = viewModel::onEvent
            )
        }
        composable(
            route = NavigationRoute.Detail.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    defaultValue = "-1"
                },
                navArgument("title") { type = NavType.StringType },
            )
        ) {
            val id = it.arguments?.getString("id") ?: "-1"
            val title  = it.arguments?.getString("title")
            DetailScreen()
        }
    }
}


//private fun NavHostController.navigateToSingleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}