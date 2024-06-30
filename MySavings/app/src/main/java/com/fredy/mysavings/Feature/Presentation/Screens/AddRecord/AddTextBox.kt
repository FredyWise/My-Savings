package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.TextBox

@Composable
fun AddTextBox(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(MaterialTheme.colorScheme.secondary) + MaterialTheme.typography.bodyMedium,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    buttonColor: Color = Color.Unspecified,
    buttonIconColor: Color = MaterialTheme.colorScheme.onSurface,
    hintText: String = "",
    value: String,
    onValueChanged: (String) -> Unit,
    isImageExist: Boolean,
    onImageButtonClick: () -> Unit,
    onCameraButtonClick: () -> Unit,
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
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
        ) {
            if (isImageExist) {
                Button(
                    onClick = onImageButtonClick,
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Show Image",
                        tint = buttonIconColor
                    )
                }
            }
            Button(
                onClick = onCameraButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera",
                    tint = buttonIconColor
                )
            }
        }
    }
}

