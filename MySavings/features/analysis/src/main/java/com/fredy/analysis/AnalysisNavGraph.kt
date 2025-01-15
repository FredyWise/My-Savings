package com.fredy.analysis

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.analysis.ui.AnalysisAccount
import com.fredy.analysis.ui.AnalysisFlow
import com.fredy.analysis.ui.AnalysisOverview
import com.fredy.analysis.viewModel.AnalysisState
import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Category
import com.fredy.domain.model.Wallet


@Composable
fun AnalysisNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    state: AnalysisState,
    recordType: RecordType,
    toggleAnalysisType: () -> Unit,
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
                recordType =recordType ,
                toggleAnalysisType = toggleAnalysisType,
                onGetCategoryDetails = onGetCategoryDetails
            )
        }

        composable(
            route = AnalysisNavigationRoute.AnalysisFlow.route
        ) {
            AnalysisFlow(
                state = state, recordType = recordType, toggleAnalysisType = toggleAnalysisType
            )
        }

        composable(
            route = AnalysisNavigationRoute.AnalysisWallet.route
        ) {

            AnalysisAccount(
                state = state,
                toggleAnalysisType = toggleAnalysisType,
                onGetWalletDetails = onGetWalletDetails
            )
        }
    }
}
