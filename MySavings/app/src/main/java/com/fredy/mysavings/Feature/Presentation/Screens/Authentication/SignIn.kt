package com.fredy.mysavings.Feature.Presentation.Screens.Authentication

import com.fredy.mysavings.Util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.Feature.Data.Enum.AuthMethod
import com.fredy.mysavings.R
import com.fredy.mysavings.Feature.Domain.Util.Resource

import com.fredy.mysavings.Util.isValidLogin
import com.fredy.mysavings.Util.isValidPhoneNumber
import com.fredy.mysavings.Feature.Presentation.ViewModels.AuthViewModel.AuthState
import com.fredy.mysavings.Feature.Presentation.ViewModels.AuthViewModel.AuthEvent
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation.NavigationRoute
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn(
    navController: NavHostController,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onPrimaryColor: Color = MaterialTheme.colorScheme.onPrimary,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    isUsingBioAuth: Boolean = false,
    state: AuthState,
    onEvent: (AuthEvent) -> Unit
) {
    val context = LocalContext.current
    var switchState by rememberSaveable {
        mutableStateOf(
            false
        )
    }
    var emailOrPhone by rememberSaveable {
        mutableStateOf(
            ""
        )
    }
    var password by rememberSaveable {
        mutableStateOf(
            ""
        )
    }
    var otpValue by rememberSaveable {
        mutableStateOf(
            ""
        )
    }

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
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
                AuthEvent.GoogleAuth(
                    credentials
                )
            )
        } catch (it: ApiException) {
            Log.e("SignIn Error: " + it)
            print(it)
        }
    }


    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                isSheetOpen = false
            },
            dragHandle = {},
        ) {
            OTPScreen(
                isLoading = state.authResource is Resource.Loading && state.authType == AuthMethod.PhoneOTP,
                phoneNumber = emailOrPhone,
                onResendOtp = {
                    onEvent(
                        AuthEvent.SendOtp(
                            context,
                            emailOrPhone,
                            onCodeSent = {
                                isSheetOpen = true
                            },
                        )
                    )
                },
                onOtpValueChange = { value -> otpValue = value },
                onOtpSignInClick = {
                    onEvent(
                        AuthEvent.VerifyPhoneNumber(
                            context, otpValue
                        )
                    )
                    isSheetOpen = false
                },
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 30.dp, end = 30.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineMedium,
            color = onBackgroundColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Enter your credential's to sign in",
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleMedium,
            color = onBackgroundColor.copy(0.7f),
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            label = if (switchState) "Email" else "Phone Number",
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            placeholder = if (switchState) "type your email..." else "type your phone number...",
            keyboardType = if (switchState) KeyboardType.Email else KeyboardType.Phone
        )
        AnimatedVisibility(
            visible = switchState,
            enter = fadeIn() + expandVertically(
                animationSpec = tween(300)
            )
        ) {
            CustomTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "typePassword...",
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = {
                    if (switchState) {
                        if (isValidLogin(emailOrPhone,password)) {
                            onEvent(
                                AuthEvent.LoginUser(
                                    emailOrPhone, password
                                )
                            )
                        } else {
                            Toast.makeText(context, "Email or Password is Invalid", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (isValidPhoneNumber(emailOrPhone)) {
                            onEvent(
                                AuthEvent.SendOtp(
                                    context,
                                    emailOrPhone,
                                    onCodeSent = {
                                        isSheetOpen = true
                                    },
                                )
                            )
                        } else {
                            Toast.makeText(context, "Phone Number should contain country code", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = primaryColor.copy(
                        0.7f
                    )
                ),
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(15.dp)
            ) {
                if (state.authResource is Resource.Loading && (state.authType == AuthMethod.Email || state.authType == AuthMethod.SendOTP)) {
                    CircularProgressIndicator(
                        color = onPrimaryColor
                    )
                } else {
                    Text(
                        text = if (switchState) "Sign In" else "Get OTP",
                        style = MaterialTheme.typography.titleMedium,
                        color = onPrimaryColor,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            if (isUsingBioAuth) {
                Spacer(modifier = Modifier.weight(0.02f))
                Button(
                    onClick = {
                        onEvent(AuthEvent.BioAuth)
                    },
                    modifier = Modifier.weight(0.2f),
                    contentPadding = PaddingValues(10.dp),
                    shape = RoundedCornerShape(15.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "fingerprint",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sign In with ${if (switchState) "Phone Number" else "Email"} instead? ",
            modifier = Modifier.clickable {
                switchState = !switchState
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
        Text(
            text = "or connect with",
            fontWeight = FontWeight.Medium,
            color = onBackgroundColor.copy(0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        GoogleButton(
            text = "Sign In With Google",
            loadingText = "Signing In ...",
            isLoading = state.authResource is Resource.Loading && state.authType == AuthMethod.Google
        ) {
            val gso = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            ).requestEmail().requestIdToken(
                context.getString(R.string.web_client_id)
            ).build()

            val googleSignInClient = GoogleSignIn.getClient(
                context, gso
            )
            launcher.launch(googleSignInClient.signInIntent)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "New User? Sign Up ",
            modifier = Modifier.clickable {
                navController.navigate(
                    NavigationRoute.SignUp.route
                )
            },
            fontWeight = FontWeight.Bold,
            color = onBackgroundColor,
        )
    }
}