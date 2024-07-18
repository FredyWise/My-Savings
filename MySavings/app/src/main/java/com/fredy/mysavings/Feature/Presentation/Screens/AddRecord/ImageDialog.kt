package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleAlertDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageDialog(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    isShowImage: Boolean = false,
    capturedImageUri: Uri,
    onDismissRequest: () -> Unit
) {
    if (isShowImage) {
        SimpleAlertDialog(
            modifier = modifier,
            title = "Captured Image", onDismissRequest = onDismissRequest,
            rightButton = {
                Button(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.small
                        ),
                    onClick = onDismissRequest,
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = "Close"
                    )
                }
            },
        ) {
            Column {
                if (capturedImageUri != Uri.EMPTY) {
                    Image(
                        contentDescription = "Captured Image",
                        painter = rememberAsyncImagePainter(
                            capturedImageUri
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onDoubleClick = onDismissRequest,
                                onLongClick = onDismissRequest,
                            ) { }
                    )
                } else {
                    Icon(
                        contentDescription = "Captured Image",
                        imageVector = Icons.Default.ImageNotSupported,
                        tint = onBackground,
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
            }
        }
    }
}