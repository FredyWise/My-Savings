package com.fredy.mysavings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController

import com.fredy.mysavings.Feature.Presentation.ViewModels.AuthViewModel.AuthViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.AuthViewModel.AuthEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.PreferencesViewModel.PreferencesViewModel
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation.Graph
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation.NavGraphRoot
import com.fredy.mysavings.ui.theme.MySavingsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<PreferencesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.state.value.updated
            }
        }
        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val setting by viewModel.state.collectAsStateWithLifecycle()
            val state by authViewModel.state.collectAsStateWithLifecycle()

            if (setting.updated) {
                if (!setting.autoLogin) {
                    authViewModel.onEvent(AuthEvent.SignOut)
                }
                MySavingsTheme(state = setting) {
                    val navController = rememberNavController()
                    val startDestination =
                        if (state.signedInUser != null && setting.autoLogin && !setting.bioAuth) Graph.MainNav else Graph.AuthNav
                    NavGraphRoot(navController, startDestination, viewModel, authViewModel)
                }
            }
        }
    }


}
