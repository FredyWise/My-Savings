package com.fredy.mysavings.ui.Screens.AuthScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    var text by remember { mutableStateOf(value) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onValueChange(it)
        },
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedTextColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondary.copy(
                0.9f
            ),
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary.copy(
                0.7f
            ),
            disabledLabelColor = MaterialTheme.colorScheme.onSecondary.copy(
                0.7f
            ),
            focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,

            ),
        label = {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    0.7f
                )
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSecondary
            )
        },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
    )

}


data class OtpValue(
    val value: String = "",
    var focused: Boolean = false,
    val focusRequester: FocusRequester? = null,
)

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    errorColor: Color = Color(0xFFFF6161),
    successColor: Color = Color(0xFF29B96F),
    focusedColor: Color = MaterialTheme.colorScheme.secondary,
    cursorColor: Color = MaterialTheme.colorScheme.secondary,
    unFocusedColor: Color = Color.Gray,
    textStyle: TextStyle = TextStyle(
        fontSize = 25.sp,
        textAlign = TextAlign.Center
    ),
    length: Int = 6,
    onOtpChange: (value: String, finished: Boolean) -> Unit = { _, _ -> },
    onFinish: (String) -> Unit = {},
    error: Boolean = false,
    success: Boolean = false,
) {
    val otpState = remember {
        mutableStateListOf(
            *MutableList(length) {
                OtpValue(focused = it == 0)
            }.toTypedArray()
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            (0 until length).forEach {
                OtpBox(
                    textStyle = textStyle,
                    unFocusedColor = unFocusedColor,
                    focusedColor = focusedColor,
                    cursorColor = cursorColor,
                    otpValue = otpState[it],
                    onValueChange = { newValue ->
                        if (otpState[it].value != newValue) {
                            if (newValue.length <= 1) {
                                otpState[it] = otpState[it].copy(
                                    value = newValue
                                )
                                if (newValue.length == 1) {
                                    if (otpState.size - 1 > it) {
                                        otpState[it + 1].focusRequester?.requestFocus()
                                    }
                                }
                            }
                            var otp = ""
                            otpState.forEach {
                                otp += it.value
                            }
                            if (otp.length >= length) onFinish(
                                otp
                            )
                            onOtpChange(
                                otp,
                                otp.length >= length
                            )
                        }
                    },
                    onFocusChanged = { focused ->
                        otpState[it] = otpState[it].copy(
                            focused = focused
                        )
                    },
                    onFocusSet = { focus ->
                        otpState[it] = otpState[it].copy(
                            focusRequester = focus
                        )
                    },
                    onBackSpace = {
                        if (otpState[it].value.isEmpty()) {
                            if (it != 0) {
                                otpState[it - 1].focusRequester?.requestFocus()
                            }
                        }
                    },
                    error = error,
                    success = success,
                    modifier = modifier,
                    errorColor = errorColor,
                    successColor = successColor
                )
            }
        }
    }
}


@Composable
fun OtpBox(
    modifier: Modifier,
    otpValue: OtpValue,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onBackSpace: () -> Unit,
    textStyle: TextStyle = TextStyle(
        fontSize = 25.sp,
        textAlign = TextAlign.Center
    ),
    onFocusSet: (FocusRequester) -> Unit,
    successColor: Color = Color(0xffffffff),
    errorColor: Color = Color(0xffffffff),
    focusedColor: Color = MaterialTheme.colorScheme.secondary,
    cursorColor: Color = MaterialTheme.colorScheme.secondary,
    unFocusedColor: Color = Color.Gray,
    error: Boolean,
    success: Boolean
) {
    val focusRequester = remember {
        FocusRequester()
    }
    LaunchedEffect(Unit) {
        onFocusSet(focusRequester)
    }
    val color by animateColorAsState(
        targetValue = if (error) errorColor
        else if (success) successColor
        else if (otpValue.focused) focusedColor
        else unFocusedColor,
        animationSpec = tween(300)
    )
    val textColor by animateColorAsState(
        targetValue = if (otpValue.focused) focusedColor else unFocusedColor,
        animationSpec = tween(300)
    )
    Box(
        modifier = modifier
            .border(
                (1.5).dp,
                color,
                RoundedCornerShape(8)
            )
            .size(50.dp, 70.dp),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            modifier = Modifier
                .focusRequester(
                    focusRequester
                )
                .onFocusChanged {
                    onFocusChanged(
                        it.isFocused
                    )
                }
                .onKeyEvent {
                    if (it.key.keyCode == Key.Backspace.keyCode) {
                        onBackSpace()
                    }
                    false
                },
            value = otpValue.value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            textStyle = textStyle.copy(color = textColor),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = cursorColor
            ),
        )
    }
}