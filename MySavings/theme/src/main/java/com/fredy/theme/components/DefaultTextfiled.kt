package com.fredy.theme.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.fredy.theme.DefaultTheme
import com.fredy.theme.util.DefaultPreview

@Composable
fun DefaultTextFiled(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    isPasswordField: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    onDone: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        isError = errorMessage != null,
//        readOnly = isClickOnly,
//        enabled = !isClickOnly,
        supportingText = errorMessage?.let {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        visualTransformation = if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
        label = label?.let {
            {
                Text(
                    text = label
                )
            }
        },
        placeholder = placeholder?.let {
            { Text(text = placeholder) }
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = placeholder,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingIcon = errorMessage?.let {
            {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onDone()
            },
            onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            },
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardType,
        ),
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        textStyle = textStyle,
        prefix = prefix,
        suffix = suffix,
        maxLines = maxLines,
        minLines = minLines,
    )
}


@Composable
@DefaultPreview
private fun DefaultTextFiledPreview() {
    DefaultTheme {
        Surface {
            DefaultTextFiled(
                value = "hey Tayo",
                onValueChange = {},
//                errorMessage = "Error message"
            )
        }
    }
}