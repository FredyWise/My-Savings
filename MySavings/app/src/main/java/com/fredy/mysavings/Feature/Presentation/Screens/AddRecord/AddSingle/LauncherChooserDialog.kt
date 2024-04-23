package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddSingle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleAlertDialog

@Composable
fun LauncherChooserDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onChooseCamera: () -> Unit,
    onChooseGallery: () -> Unit,
) {
    SimpleAlertDialog(
        modifier = modifier, title = "Pick Image From: ", onDismissRequest = onDismissRequest,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Icon(
                modifier = Modifier
                    .size(75.dp)
                    .clickable {
                        onChooseCamera()
                    },
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Camera"
            )
            Icon(
                modifier = Modifier
                    .size(75.dp)
                    .clickable {
                        onChooseGallery()
                    },
                imageVector = Icons.Default.Image,
                contentDescription = "Image"
            )
        }
    }
}