package com.fredy.mysavings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.fredy.mysavings.ui.screens.AddScreen
import com.fredy.mysavings.ui.theme.MySavingsTheme

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MySavingsTheme {
                // A surface container using the 'background' color from the theme
//                MySavingsApp()
                AddScreen()
            }
        }
    }
}
