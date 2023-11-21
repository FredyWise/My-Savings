package com.example.myapplication.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun SignUp(gotoSignIn: () -> Unit) {
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember {
        mutableStateOf(
            ""
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colors.background
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            CustomTextField(
                label = "Username",
                value = username.value,
                onValueChange = { username.value = it },
                placeholder = "typeUsername..."
            )

            CustomTextField(
                label = "Email",
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "typeEmail...",
                keyboardType = KeyboardType.Email
            )

            CustomTextField(
                label = "Password",
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "typePassword...",
                keyboardType = KeyboardType.Password
            )

            CustomTextField(
                label = "Confirm Password",
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                placeholder = "typeConfirmPassword...",
                keyboardType = KeyboardType.Password
            )

            Button(
                onClick = {
                    //do some logic
                    gotoSignIn()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 16.dp
                    )
                    .height(55.dp),
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = MaterialTheme.typography.h6.fontSize,
                    style = MaterialTheme.typography.button
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Already have an account? Sign in instead!",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.clickable(
                    onClick = {
                        gotoSignIn()
                    })
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.onSurface.copy(
                    alpha = 0.08f
                )
            )
        }
    }
}



