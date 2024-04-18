package com.fredy.mysavings.Feature.Presentation.Screens.AddSingle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

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
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Unspecified,
            unfocusedIndicatorColor = Color.Unspecified
        ),
        modifier = modifier
            .background(
                backgroundColor,
                shape
            )
            .border(2.dp, borderColor, shape)
    )
}

@Composable
fun AddTextBox(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    textStyle: TextStyle = TextStyle(MaterialTheme.colorScheme.secondary) + MaterialTheme.typography.bodyMedium,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    buttonColor: Color = Color.Unspecified,
    buttonIconColor:Color = MaterialTheme.colorScheme.onSurface,
    onButtonClick: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        TextBox(
            value = value,
            modifier = Modifier.fillMaxSize(),
            onValueChanged = onValueChanged,
            hintText = hintText,
            textStyle = textStyle,
            shape = shape,
            backgroundColor = backgroundColor,
            borderColor = borderColor
        )
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Camera",tint = buttonIconColor)
        }
    }
}

