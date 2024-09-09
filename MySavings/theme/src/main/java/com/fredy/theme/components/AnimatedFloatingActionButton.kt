package com.fredy.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.fredy.theme.DefaultTheme
import com.fredy.theme.util.DefaultPreview

@Composable
fun AnimatedFloatingActionButton(
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = FloatingActionButtonDefaults.shape,
    icon: @Composable () -> Unit,
    showFab: Boolean = true,
    onShowFab: () -> Boolean = { true },
    onClick: () -> Unit,
) {
    val isAtBottomOfList by remember {
        derivedStateOf {
            showFab && onShowFab()
        }
    }
    AnimatedVisibility(
        modifier = Modifier.clip(
            shape
        ), visible = !isAtBottomOfList
    ) {
        FloatingActionButton(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = contentColor.copy(
                        0.3f
                    ),
                    shape = shape
                )
                .then(modifier),
            shape = shape,
            contentColor = contentColor,
            containerColor = containerColor,
            onClick = onClick,
        ) {
            icon()
        }
    }
}

@Composable
@DefaultPreview
private fun AppBarPreview() {
    DefaultTheme {
        Surface {
            var onShowFab by remember {
                mutableStateOf(true)
            }

            AnimatedFloatingActionButton(
                shape = MaterialTheme.shapes.medium,
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Chat",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                onShowFab = { onShowFab },
                onClick = {
                    onShowFab = !onShowFab
                },
            )

        }
    }
}