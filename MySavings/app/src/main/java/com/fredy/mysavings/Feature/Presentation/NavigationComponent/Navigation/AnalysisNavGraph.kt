package com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletState
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryState
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordState
import com.fredy.mysavings.Feature.Presentation.Screens.Record.Analysis.AnalysisAccount
import com.fredy.mysavings.Feature.Presentation.Screens.Record.Analysis.AnalysisFlow
import com.fredy.mysavings.Feature.Presentation.Screens.Record.Analysis.AnalysisOverview


@Composable
fun AnalysisNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    state: RecordState,
    onEvent: (RecordEvent) -> Unit,
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
