package com.fredy.askquestions.features.ui.screens.comonComponents.Handler

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun AsyncImageHandler(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    imageScale: ContentScale = ContentScale.Crop,
    contentDescription: String = "",
    imageVector: ImageVector,
    imageVectorColor: Color = MaterialTheme.colorScheme.onSurface,

    ) {
    if (!imageUrl.isNullOrEmpty() && !imageUrl.contains("null", true)) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = imageScale
        )
    } else {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = imageVectorColor,
            modifier = modifier,
        )
    }
}


