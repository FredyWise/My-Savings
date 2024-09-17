package com.fredy.mysavings.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.analysis.ui.AnalysisScreen
import com.fredy.analysis.viewModel.AnalysisViewModel
import com.fredy.book.ui.RecordsScreen
import com.fredy.book.viewModel.BookViewModel
import com.fredy.book.viewModel.RecordEvent
import com.fredy.book.viewModel.RecordViewModel
import com.fredy.category.ui.CategoriesScreen
import com.fredy.category.ui.CategoryDetailBottomSheet
import com.fredy.category.viewModel.CategoryEvent
import com.fredy.category.viewModel.CategoryViewModel
import com.fredy.wallet.ui.WalletDetailBottomSheet
import com.fredy.wallet.ui.WalletsScreen
import com.fredy.wallet.viewModel.WalletEvent
import com.fredy.wallet.viewModel.WalletViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MainNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    recordViewModel: RecordViewModel,
    analysisViewModel: AnalysisViewModel,
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
                state = state,
                onEvent = recordViewModel::onEvent,
                bookState = bookState,
                bookEvent = bookViewModel::onEvent,
                onAddRecord = {
                    rootNavController.navigate(
                        "${NavigationRoute.Add.route}?bookId=${state.filterState.currentBook?.bookId}"
                    )
                }
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
            val state by analysisViewModel.state.collectAsStateWithLifecycle()
            val recordEvent = recordViewModel::onEvent
            val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
            val walletEvent = walletViewModel::onEvent
            val walletState by walletViewModel.state.collectAsStateWithLifecycle()
            val categoryEvent = categoryViewModel::onEvent
            var isCategorySheetOpen by rememberSaveable {
                mutableStateOf(false)
            }
            CategoryDetailBottomSheet(
                isSheetOpen = isCategorySheetOpen,
                onCloseBottomSheet = { isCategorySheetOpen = it },
                state = categoryState,
                onShowRecordDialog = { item ->
                    recordEvent(
                        RecordEvent.ShowDialog(item)
                    )
                },
            )
            var isWalletSheetOpen by rememberSaveable {
                mutableStateOf(false)
            }
            WalletDetailBottomSheet(
                isSheetOpen = isWalletSheetOpen,
                onCloseBottomSheet = { isWalletSheetOpen = it },
                state = walletState,
                onShowRecordDialog = { item ->
                    recordEvent(
                        RecordEvent.ShowDialog(item)
                    )
                },
            )
            AnalysisScreen(
                rootNavController = rootNavController,
                state = state,
                onEvent = analysisViewModel::onEvent,
                onGetCategoryDetails = { category ->
                    categoryEvent(CategoryEvent.GetCategoryDetail(category))
                    isCategorySheetOpen = true

                },
                onGetWalletDetails = { wallet ->
                    walletEvent(WalletEvent.GetWalletDetail(wallet))
                    isWalletSheetOpen = true
                }

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
            val recordEvent = recordViewModel::onEvent
            WalletsScreen(
                modifier = Modifier.padding(8.dp),
                state = state,
                onEvent = walletViewModel::onEvent,
                onUpdateRecord = {
                    recordEvent(RecordEvent.UpdateRecord)
                },
                onShowRecordDialog = { item ->
                    recordEvent(
                        RecordEvent.ShowDialog(item)
                    )
                },
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
            val recordEvent = recordViewModel::onEvent
            CategoriesScreen(
                modifier = Modifier.padding(8.dp),
                state = state,
                onEvent = categoryViewModel::onEvent,
                onUpdateRecord = {
                    recordEvent(RecordEvent.UpdateRecord)
                },
                onShowRecordDialog = { item ->
                    recordEvent(
                        RecordEvent.ShowDialog(item)
                    )
                },
            )
        }
    }
}


//private fun NavHostController.navigateToSingleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}