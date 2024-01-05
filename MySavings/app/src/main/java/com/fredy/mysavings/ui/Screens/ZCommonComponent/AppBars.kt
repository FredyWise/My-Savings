package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DetailAppBar(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    title: String,
    onNavigationIconClick: () -> Unit,
    icon: Int,
    iconDescription: String,
    itemName: String,
    itemInfo: String,
    content: @Composable () -> Unit,
) {
    DefaultAppBar(
        modifier = modifier,
        title = title,
        onNavigationIconClick = onNavigationIconClick
    ) {
        SimpleEntityItem(
            icon = icon,
            iconModifier = Modifier
                .size(
                    25.dp
                )
                .clip(
                    shape = MaterialTheme.shapes.small
                ),
            iconDescription = iconDescription
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(
                    vertical = 3.dp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = itemInfo,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(
                    vertical = 3.dp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigationIconClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
                Text(text = title)
            },
            navigationIcon = {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Close"
                    )
                }
            },
        )
        content()
    }
}