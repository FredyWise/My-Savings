package com.fredy.mysavings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.DI.AppModuleImpl
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavGraphRoot
import com.fredy.mysavings.ui.theme.MySavingsTheme

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MySavingsTheme {
                val navController = rememberNavController()
                NavGraphRoot(navController)
            }
        }
        AppModuleImpl.provideAppContext(this)
    }
}
