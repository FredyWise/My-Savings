package com.fredy.ui.components.button


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fredy.ui.components.list.SimpleEntityItem

@Composable
fun SimpleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    image: Int? = null,
    imageDescription: String = "",
    imageColor: Color = Color.Unspecified,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        SimpleEntityItem(
            modifier = Modifier.padding(
                8.dp
            ),
            iconModifier = Modifier
                .size(40.dp)
                .padding(end = 4.dp),
            icon = image,
            iconDescription = imageDescription,
            iconColor = imageColor,
            contentWeight = 0f
        ) {
            Text(
                text = title,
                style = titleStyle,
                color = titleColor,
                maxLines = 1
            )
        }
    }
}

