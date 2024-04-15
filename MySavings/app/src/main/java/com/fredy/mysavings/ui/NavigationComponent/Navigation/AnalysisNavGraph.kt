package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.mysavings.ViewModels.WalletState
import com.fredy.mysavings.ViewModels.CategoryState
import com.fredy.mysavings.ViewModels.Event.WalletEvent
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.Screens.Analysis.AnalysisAccount
import com.fredy.mysavings.ui.Screens.Analysis.AnalysisFlow
import com.fredy.mysavings.ui.Screens.Analysis.AnalysisOverview


@Composable
fun AnalysisNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    walletState: WalletState,
    accountEvent: (WalletEvent) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.AnalysisOverview.route,
        modifier = modifier
    ) {
        composable(
            route = NavigationRoute.AnalysisOverview.route
        ) {
            AnalysisOverview(
                state = state,
                onEvent = onEvent,
                categoryState = categoryState,
                categoryEvent = categoryEvent,
            )
        }

        composable(
            route = NavigationRoute.AnalysisFlow.route
        ) {
            AnalysisFlow(
                state = state, onEvent = onEvent
            )
        }

        composable(
            route = NavigationRoute.AnalysisWallet.route
        ) {
            AnalysisAccount(
                state = state,
                onEvent = onEvent,
                walletState = walletState,
                accountEvent = accountEvent
            )
        }
    }
}
