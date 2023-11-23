package com.example.myapplication.ui.screens.authentication

//import com.fredy.mysavings.ViewModels.AuthState
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModels.AuthState
import com.fredy.mysavings.ViewModels.Event.SignInEvent
import com.fredy.mysavings.ViewModels.Event.SignUpEvent
import com.fredy.mysavings.ViewModels.GoogleSignInState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.NavigationComponent.Navigation.navigateSingleTopTo
import com.fredy.mysavings.ui.Screens.AuthScreen.CustomTextField
import com.fredy.mysavings.ui.Screens.AuthScreen.GoogleButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SignUp(
    navController: NavHostController,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onPrimaryColor: Color = MaterialTheme.colorScheme.onPrimary,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    state: AuthState?,
    googleSignInState:GoogleSignInState,
    onEvent: (SignUpEvent) -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember {
        mutableStateOf(
            ""
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val account = GoogleSignIn.getSignedInAccountFromIntent(
            it.data
        )
        try {
            val result = account.getResult(
                ApiException::class.java
            )
            val credentials = GoogleAuthProvider.getCredential(
                result.idToken, null
            )
            onEvent(
                SignUpEvent.googleSignIn(
                    credentials
                )
            )
        } catch (it: ApiException) {
            print(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                18.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineMedium,
            color = onBackgroundColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Enter your credential's to register an account",
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleMedium,
            color = onBackgroundColor.copy(0.7f),
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Username",
            value = username,
            onValueChange = { username = it },
            placeholder = "typeUsername..."
        )

        CustomTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "typeEmail...",
            keyboardType = KeyboardType.Email
        )

        CustomTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "typePassword...",
            keyboardType = KeyboardType.Password
        )

        CustomTextField(
            label = "Confirm Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "typeConfirmPassword...",
            keyboardType = KeyboardType.Password
        )

        Button(
            onClick = {
                if (password == confirmPassword) {
                    onEvent(SignUpEvent.registerUser(email, password))
                    navController.navigateUp()
                }else {
                    Toast.makeText(
                        context,
                        "Passwords are not the same",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            },
            enabled = email.isNotEmpty() && username.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 20.dp,
                    start = 30.dp,
                    end = 30.dp
                ),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = primaryColor,
                contentColor = onPrimaryColor
            ),
            shape = RoundedCornerShape(15.dp)
        ) {
            if (state?.isLoading == true) {
                CircularProgressIndicator(color = onPrimaryColor)
            } else {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleMedium,
                    color = onPrimaryColor,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Already have an account? Sign In ",
            modifier = Modifier.clickable {
                navController.navigateUp()
            },
            fontWeight = FontWeight.Bold,
            color = onBackgroundColor,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.08f
            )
        )
        GoogleButton(
            text = "Sign Up With Google",
            loadingText = "Signing Up ...",
            isLoading = googleSignInState.loading
        ) {
            val gso = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            ).requestEmail().requestIdToken(
                context.getString(R.string.web_client_id)
            ).build()

            val googleSignInClient = GoogleSignIn.getClient(
                context,
                gso
            )
            launcher.launch(googleSignInClient.signInIntent)
        }
    }

}



