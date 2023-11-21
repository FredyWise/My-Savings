package com.example.myapplication.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.ui.Screens.AuthScreen.CustomTextField

@Composable
fun SignIn(
    gotoHome: () -> Unit,
    gotoSignUp: () -> Unit,
    gotoForgotPassword: () -> Unit
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            CustomTextField(
                label = "Username",
                value = username.value,
                onValueChange = { username.value = it },
                placeholder = "typeUsername..."
            )
            CustomTextField(
                label = "Password",
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "typePassword...",
                keyboardType = KeyboardType.Password
            )
            Button(
                onClick = {
                    gotoHome()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(55.dp),
            ) {
                Text(
                    text = "Sign In",
                    fontSize = MaterialTheme.typography.h6.fontSize,
                    style = MaterialTheme.typography.button
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Don't have an account? Sign up now!",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.clickable(onClick = {
                    gotoSignUp()
                })
            )
            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.clickable(onClick = {
                    gotoForgotPassword()
                })
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
            )
        }
    }
}
