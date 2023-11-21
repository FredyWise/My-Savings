package com.fredy.mysavings.ui.Screens.AuthScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    enabled: Boolean = true,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    var text by remember { mutableStateOf(value) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onValueChange(it)
        },
        placeholder = { Text(text = placeholder) },
        enabled = enabled,
        label = {
            Text(text = label)
        },
        singleLine = singleLine,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

