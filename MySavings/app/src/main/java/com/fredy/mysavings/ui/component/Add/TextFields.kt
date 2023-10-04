package com.fredy.mysavings.ui.component.Add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextBox(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    textStyle: TextStyle = TextStyle(MaterialTheme.colorScheme.secondary) + MaterialTheme.typography.bodyMedium,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
) {
    TextField(
        value = value,
        onValueChange = onValueChanged,
        textStyle = textStyle,
        placeholder = {
            Text(
                text = hintText
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Unspecified,
            unfocusedIndicatorColor = Color.Unspecified
        ),
        modifier = modifier
            .background(
                backgroundColor,
                shape
            )
            .border(2.dp,borderColor,shape)
    )
}