package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.ViewModels.Event.RecordsEvent

@Composable
fun DisplayBar(
    modifier: Modifier = Modifier,
    selectedData: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface
            )
            .padding(
                horizontal = 4.dp, vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon()
        Icon(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable {
                    onPrevious()
                }
                .padding(4.dp),
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "",
            tint = tint,
        )
        Text(
            text = selectedData,
            color = tint,
        )
        Icon(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable {
                    onNext()
                }
                .padding(4.dp),
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "",
            tint = tint,
        )
        trailingIcon()
    }

}

