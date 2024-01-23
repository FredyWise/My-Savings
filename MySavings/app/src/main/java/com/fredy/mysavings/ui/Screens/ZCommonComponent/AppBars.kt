package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DetailAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    onNavigationIconClick: () -> Unit,
    icon: Int,
    iconDescription: String,
    itemName: String,
    itemInfo: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                backgroundColor
            )
            .padding(top = 24.dp)
            .padding(
                horizontal = 8.dp
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(modifier = Modifier
                .padding(
                    horizontal = 8.dp
                )
                .clip(
                    shape = CircleShape
                )
                .clickable { onNavigationIconClick() }
                .size(
                    35.dp
                ),
                tint = onBackgroundColor,
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Close")
            Text(
                modifier = Modifier.padding(
                    vertical = 8.dp
                ),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        SimpleEntityItem(
            modifier = Modifier.padding(8.dp),
            icon = icon,
            iconModifier = Modifier
                .size(
                    55.dp
                )
                .clip(
                    shape = MaterialTheme.shapes.small
                ),
            iconDescription = iconDescription
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.titleLarge,
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
                style = MaterialTheme.typography.titleMedium,
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
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    onNavigationIconClick: () -> Unit,
    actionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
                Text(text = title)
            },
            actions = { actionButton() },
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .padding(
                            horizontal = 4.dp
                        )
                        .clip(CircleShape)
                        .clickable {
                            onNavigationIconClick()
                        }
                        .padding(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Close",
                        tint = iconColor
                    )
                }
            },
        )
        content()
    }
}