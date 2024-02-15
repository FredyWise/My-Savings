package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    recordViewModel: RecordViewModel = hiltViewModel(),
    analysisViewModel: AnalysisViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Records.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
    ) {
        composable(
            route = NavigationRoute.Records.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
        ) {
            val state by recordViewModel.state.collectAsStateWithLifecycle()

            RecordsScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = recordViewModel::onEvent,
            )
        }
        composable(
            route = NavigationRoute.Analysis.route,
        ) {
            val state by analysisViewModel.state.collectAsStateWithLifecycle()

            AnalysisScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = analysisViewModel::onEvent,
            )
        }
        composable(
            route = NavigationRoute.Account.route,
        ) {
            val state by accountViewModel.state.collectAsStateWithLifecycle()

            AccountsScreen(
                modifier = Modifier.padding(8.dp),
                state = state,
                onEvent = accountViewModel::onEvent,
            )
        }
        composable(
            route = NavigationRoute.Categories.route,
        ) {
            val state by categoryViewModel.state.collectAsStateWithLifecycle()

            CategoriesScreen(
                modifier = Modifier.padding(8.dp),
                state = state,
                onEvent = categoryViewModel::onEvent,
            )
        }
    }
}


//private fun NavHostController.navigateToSingleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}