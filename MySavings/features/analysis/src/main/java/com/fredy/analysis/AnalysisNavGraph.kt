package com.fredy.analysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.analysis.ui.AnalysisAccount
import com.fredy.analysis.ui.AnalysisFlow
import com.fredy.analysis.ui.AnalysisOverview
import com.fredy.analysis.viewModel.AnalysisEvent
import com.fredy.analysis.viewModel.AnalysisState
import com.fredy.domain.model.Category
import com.fredy.domain.model.Wallet


@Composable
fun AnalysisNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    state: AnalysisState,
    onEvent: (AnalysisEvent) -> Unit,
    onGetCategoryDetails: (Category) -> Unit,
    onGetWalletDetails: (Wallet) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = AnalysisNavigationRoute.AnalysisOverview.route,
        modifier = modifier
    ) {
        composable(
            route = AnalysisNavigationRoute.AnalysisOverview.route
        ) {
            AnalysisOverview(
                state = state,
                onEvent = onEvent,
                onGetCategoryDetails = onGetCategoryDetails
            )
        }

        composable(
            route = AnalysisNavigationRoute.AnalysisFlow.route
        ) {
            AnalysisFlow(
                state = state, onEvent = onEvent
            )
        }

        composable(
            route = AnalysisNavigationRoute.AnalysisWallet.route
        ) {

            AnalysisAccount(
                state = state,
                onEvent = onEvent,
                onGetWalletDetails = onGetWalletDetails
            )
        }
    }
}
