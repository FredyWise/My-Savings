package com.fredy.mysavings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.ViewModels.SettingViewModel
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavGraphRoot
import com.fredy.mysavings.ui.theme.MySavingsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingViewModel = hiltViewModel()
            val setting by viewModel.state.collectAsStateWithLifecycle()
            if (setting.updated) {
                MySavingsTheme(state = setting) {
                    val navController = rememberNavController()
                    NavGraphRoot(navController, viewModel)
                }
            }

        }
    }
}
