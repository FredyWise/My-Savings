package com.fredy.mysavings.Feature.Presentation.Navigation

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
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordViewModel
import com.fredy.mysavings.Feature.Presentation.Screens.Wallet.WalletsScreen
import com.fredy.mysavings.Feature.Presentation.Screens.Record.Analysis.AnalysisScreen
import com.fredy.mysavings.Feature.Presentation.Screens.Category.CategoriesScreen
import com.fredy.mysavings.Feature.Presentation.Screens.Record.MainScreen.RecordsScreen
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