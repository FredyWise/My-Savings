package com.fredy.mysavings.ui.component.Add

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ButtonWithTitleAndIcon(
    buttonTitle: String = "Title",
    buttonTitleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    buttonTitleColor: Color = MaterialTheme.colorScheme.onBackground,
    buttonIcon: ImageVector? = null,
    buttonIconDescription: String? = null,
    buttonIconColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonText: String = "RepresentIcon",
    buttonTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    buttonTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonShape: Shape = MaterialTheme.shapes.small,
    buttonBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    borderColor: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buttonTitle,
            style = buttonTitleStyle,
            color = buttonTitleColor,
            modifier = Modifier.padding(3.dp)
        )
        ButtonWithIcon(
            buttonIcon = buttonIcon,
            buttonIconDescription = buttonIconDescription,
            buttonIconColor = buttonIconColor,
            buttonText = buttonText,
            buttonTextStyle = buttonTextStyle,
            buttonTextColor = buttonTextColor,
            buttonShape = buttonShape,
            buttonBackgroundColor = buttonBackgroundColor,
            borderColor = borderColor,
            onClick = onClick,
            modifier = Modifier
                .padding(vertical = 10.dp)
        )

    }
}

@Composable
fun ButtonWithIcon(
    buttonIcon: ImageVector? = null,
    buttonIconDescription: String? = null,
    buttonIconColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonText: String = "RepresentIcon",
    buttonTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    buttonTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonShape: Shape = MaterialTheme.shapes.small,
    buttonBackgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    spacing: Dp = 8.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                buttonBackgroundColor, buttonShape
            )
            .clickable {
                onClick()
            }
            .then(modifier)
            ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                spacing
            ),
            modifier = Modifier
                .padding(5.dp)
                .border(
                    2.dp, borderColor, buttonShape
                )
        ) {
            if (buttonIcon != null) {
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = buttonIconDescription,
                    tint = buttonIconColor
                )
            }
            Text(
                text = buttonText,
                style = buttonTextStyle,
                color = buttonTextColor
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Preview(
    showBackground = true, // Show a background
    widthDp = 360, // Set the width to the real screen width
    heightDp = 640 // Set the height to the real screen height
)
@Composable
fun ButtonWithTitlePreview() {
    val screen = LocalDensity.current.run {
        LocalConfiguration.current.screenHeightDp.dp to LocalConfiguration.current.screenWidthDp.dp
    }

    // Create a background with a color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Blue
            ) // Set the background color
    ) {
        Row {
            ButtonWithTitleAndIcon(buttonTitle = "Sample Title",
                buttonText = "Click Me",
                modifier = Modifier.padding(16.dp),
                onClick = {})
            ButtonWithTitleAndIcon(buttonTitle = "Sample Title",
                buttonText = "Click Me",
                modifier = Modifier.padding(16.dp),
                onClick = {})
        }
    }
}