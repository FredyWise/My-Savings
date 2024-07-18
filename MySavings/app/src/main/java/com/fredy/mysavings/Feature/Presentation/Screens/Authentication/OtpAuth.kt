package com.fredy.mysavings.Feature.Presentation.Screens.Authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun OTPScreen(
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onPrimaryColor: Color = MaterialTheme.colorScheme.onPrimary,
    isLoading: Boolean = false,
    phoneNumber: String,
    onResendOtp: () -> Unit,
    onOtpValueChange: (value: String) -> Unit,
    onOtpSignInClick: () -> Unit
) {
    var isFinished by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(75.dp))

        Text(
            text = "OTP is send to "+phoneNumber,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OtpTextField(onOtpChange = { value, finished ->
            onOtpValueChange(value)
            isFinished = finished
        })

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Didn't get code? Resend Otp",
            modifier = Modifier.clickable {
                onResendOtp()
            },
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(75.dp))

        Button(
            enabled = isFinished,
            onClick = onOtpSignInClick,
            modifier = Modifier
                .fillMaxWidth(
                    0.8f
                )
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = primaryColor.copy(
                    0.7f
                )
            ),
            shape = RoundedCornerShape(15.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = onPrimaryColor
                )
            } else {
                Text(
                    text = "Sign In",
                    fontSize = 15.sp,
                    color = onPrimaryColor
                )
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }

}


