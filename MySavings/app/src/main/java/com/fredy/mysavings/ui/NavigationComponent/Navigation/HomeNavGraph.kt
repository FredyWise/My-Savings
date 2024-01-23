package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Records.route,
        modifier = modifier
    ) {
        composable(
            route = NavigationRoute.Records.route,
            enterTransition = {
                fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(500))
            },
        ) {
            val viewModel: RecordViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            RecordsScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = viewModel::onEvent,
            )
        }
        composable(
            route = NavigationRoute.Analysis.route,
            enterTransition = {
                fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(500))
            },
        ) {
            val viewModel: AnalysisViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            AnalysisScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = viewModel::onEvent,
            )
        }
        composable(
            route = NavigationRoute.Account.route,
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(
                        500
                    ),
                    initialOffsetY = { it }) + fadeIn(
                    animationSpec = tween(
                        500
                    )
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(
                        500
                    ),
                    targetOffsetY = { it }) + fadeOut(
                    animationSpec = tween(
                        500
                    )
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(500))
            },
        ) {
            val viewModel: AccountViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            AccountsScreen(
                modifier = Modifier.padding(8.dp),
                state = state,
                onEvent = viewModel::onEvent,
            )
        }
        composable(
            route = NavigationRoute.Categories.route,
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(
                        500
                    ),
                    initialOffsetY = { -it }) + fadeIn(
                    animationSpec = tween(
                        500
                    )
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(
                        500
                    ),
                    targetOffsetY = { -it }) + fadeOut(
                    animationSpec = tween(
                        500
                    )
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(500))
            },
        ) {
            val viewModel: CategoryViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            CategoriesScreen(
                modifier = Modifier.padding(8.dp),
                state = state,
                onEvent = viewModel::onEvent,
            )
        }
    }
}


//private fun NavHostController.navigateToSingleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}