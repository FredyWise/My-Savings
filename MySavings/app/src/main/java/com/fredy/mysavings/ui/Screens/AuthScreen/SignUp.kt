package com.example.myapplication.ui.screens.authentication

//import com.fredy.mysavings.ViewModels.AuthState
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAPhoto
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModels.AuthState
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.fredy.mysavings.ViewModels.GoogleSignInState
import com.fredy.mysavings.ui.Screens.AuthScreen.CustomTextField
import com.fredy.mysavings.ui.Screens.AuthScreen.GoogleButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import javax.security.auth.callback.Callback

@Composable
fun SignUp(
    navController: NavHostController,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onPrimaryColor: Color = MaterialTheme.colorScheme.onPrimary,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    state: AuthState?,
    googleSignInState: GoogleSignInState,
    onEvent: (AuthEvent) -> Unit
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
    var profilePictureUri by remember {
        mutableStateOf<Uri>(
            Uri.EMPTY
        )
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                profilePictureUri = it
            }
        },
    )


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
                AuthEvent.googleAuth(
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
        Box(contentAlignment = Alignment.BottomEnd) {
            if (profilePictureUri != Uri.EMPTY) {
                Image(
                    painter = rememberImagePainter(
                        profilePictureUri
                    ),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .padding(
                            horizontal = 8.dp
                        )
                        .size(125.dp)
                        .clip(
                            CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile picture",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(
                            horizontal = 8.dp
                        )
                        .size(125.dp)
                        .clip(
                            CircleShape
                        ),
                )
            }
            Icon(imageVector = Icons.Default.AddAPhoto,
                contentDescription = "add image button",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.surface
                    )
                    .padding(7.dp))
        }

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
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )

        CustomTextField(
            label = "Confirm Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "typeConfirmPassword...",
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )

        Button(
            onClick = {
                if (password == confirmPassword && password.length >= 8) {
                    onEvent(
                        AuthEvent.registerUser(
                            username = username,
                            email = email,
                            password = password,
                            photoUrl = profilePictureUri
                        )
                    )
                } else if (password.length < 8) {
                    Toast.makeText(
                        context,
                        "Password need to be at least 8 character",
                        Toast.LENGTH_LONG,
                    ).show()
                } else {
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
            text = "Already have an account? Sign In Now ",
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
        Text(
            text = "or connect with",
            fontWeight = FontWeight.Medium,
            color = onBackgroundColor.copy(0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
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
                context, gso
            )
            launcher.launch(googleSignInClient.signInIntent)
        }
    }

}


