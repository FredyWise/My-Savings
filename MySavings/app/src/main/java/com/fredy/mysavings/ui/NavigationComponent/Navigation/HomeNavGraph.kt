package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.mysavings.Util.defaultColors
import com.fredy.mysavings.ViewModels.AccountViewModel
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
    recordViewModel: RecordViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Records.route,
        modifier = modifier,
    ) {
        composable(
            route = NavigationRoute.Records.route,
            enterTransition = {
                fadeIn()
            },
            exitTransition = {
                fadeOut()
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
            enterTransition = {
                fadeIn()
            },
            exitTransition = {
                fadeOut()
            },
        ) {
            val state by recordViewModel.state.collectAsStateWithLifecycle()

            AnalysisScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = recordViewModel::onEvent,
            )
        }
        composable(
            route = NavigationRoute.Account.route,
            enterTransition = {
                fadeIn()
            },
            exitTransition = {
                fadeOut()
            },
        ) {
            val state by accountViewModel.state.collectAsStateWithLifecycle()
            val recordState by recordViewModel.state.collectAsStateWithLifecycle()

            AccountsScreen(
                modifier = Modifier.padding(horizontal = 8.dp),
                rootNavController = rootNavController,
                state = state,
                onEvent = accountViewModel::onEvent,
                recordState = recordState,
                recordEvent = recordViewModel::onEvent,
            )
        }
        composable(
            route = NavigationRoute.Categories.route,
            enterTransition = {
                fadeIn()
            },
            exitTransition = {
                fadeOut()
            },
        ) {
            val state by categoryViewModel.state.collectAsStateWithLifecycle()
            val recordState by recordViewModel.state.collectAsStateWithLifecycle()

            CategoriesScreen(
                modifier = Modifier.padding(horizontal = 8.dp),
                rootNavController = rootNavController,
                state = state,
                onEvent = categoryViewModel::onEvent,
                recordState = recordState,
                recordEvent = recordViewModel::onEvent,
            )
        }
    }
}


//private fun NavHostController.navigateToSingleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}