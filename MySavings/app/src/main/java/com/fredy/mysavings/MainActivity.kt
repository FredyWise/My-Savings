package com.fredy.mysavings

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.fredy.domain.util.ActivityProvider

import com.fredy.auth.viewModel.AuthViewModel
import com.fredy.auth.viewModel.AuthEvent
import com.fredy.mysavings.navigation.Graph
import com.fredy.mysavings.navigation.NavGraphRoot
import com.fredy.preferences.domain.isDarkMode
import com.fredy.preferences.viewModel.PreferencesViewModel
import com.fredy.theme.DefaultTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ActivityProvider {
    override fun getActivity(): Activity {
        return this
    }

    private val viewModel by viewModels<PreferencesViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.state.value.updated
            }
        }
//        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val setting by viewModel.state.collectAsStateWithLifecycle()
            val state by authViewModel.state.collectAsStateWithLifecycle()

            val isDarkTheme = if(setting.displayMode.isDarkMode() == null) isSystemInDarkTheme() else setting.displayMode.isDarkMode()!!

            if (setting.updated) {
                if (!setting.autoLogin) {
                    authViewModel.onEvent(
                        AuthEvent.SignOut
                    )
                }
                DefaultTheme(darkTheme = isDarkTheme) {
                    val navController = rememberNavController()
                    val startDestination =
                        if (state.signedInUser != null && setting.autoLogin && !setting.bioAuth) Graph.MainNav else Graph.AuthNav
                    NavGraphRoot(navController, startDestination, viewModel, authViewModel)
                }
            }
        }
    }


}
