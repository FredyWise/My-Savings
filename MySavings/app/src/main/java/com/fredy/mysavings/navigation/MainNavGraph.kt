package com.fredy.mysavings.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.analysis.ui.AnalysisScreen
import com.fredy.analysis.viewModel.RecordViewModel
import com.fredy.book.viewModel.BookViewModel
import com.fredy.category.ui.CategoriesScreen
import com.fredy.category.viewModel.CategoryViewModel
import com.fredy.book.ui.RecordsScreen
import com.fredy.wallet.ui.WalletsScreen
import com.fredy.wallet.viewModel.WalletViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MainNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    recordViewModel: RecordViewModel,
    walletViewModel: WalletViewModel,
    categoryViewModel: CategoryViewModel,
    bookViewModel: BookViewModel,
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
            val bookState by bookViewModel.state.collectAsStateWithLifecycle()

            RecordsScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = recordViewModel::onEvent,
                bookState = bookState,
                bookEvent = bookViewModel::onEvent
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
            val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
            val accountState by walletViewModel.state.collectAsStateWithLifecycle()

            AnalysisScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = recordViewModel::onEvent,
                categoryState = categoryState,
                categoryEvent = categoryViewModel::onEvent,
                walletState = accountState,
                accountEvent = walletViewModel::onEvent
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
            val state by walletViewModel.state.collectAsStateWithLifecycle()
            WalletsScreen(
                modifier = Modifier.padding(8.dp),
                rootNavController = rootNavController,
                state = state,
                onEvent = walletViewModel::onEvent,
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
            CategoriesScreen(
                modifier = Modifier.padding(8.dp),
                rootNavController = rootNavController,
                state = state,
                onEvent = categoryViewModel::onEvent,
                recordEvent = recordViewModel::onEvent,
            )
        }
    }
}


//private fun NavHostController.navigateToSingleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}