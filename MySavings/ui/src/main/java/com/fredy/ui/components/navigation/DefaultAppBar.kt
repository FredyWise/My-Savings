package com.fredy.ui.components.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp



@Composable
fun DefaultAppBar(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    onNavigationIconClick: () -> Unit,
    actionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBarTemplate(
            modifier = Modifier,
            title = title,
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close",
                        tint = iconColor
                    )
                }
            },
        )
        Column(
            modifier = contentModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}