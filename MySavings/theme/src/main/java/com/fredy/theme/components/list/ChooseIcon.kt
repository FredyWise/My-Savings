package com.fredy.theme.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fredy.theme.model.SavingsIcon


@Composable
fun ChooseIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.secondary.copy(0.3f),
    normalColor: Color = Color.Unspecified,
    onClick: (SavingsIcon) -> Unit,
    selectedIcon: Int,
    icons: List<SavingsIcon>,
) {
    LazyHorizontalGrid(
        modifier = modifier
            .height(138.dp)
            .clip(
                shape = MaterialTheme.shapes.medium
            )
            .background(
                MaterialTheme.colorScheme.background
            ),
        rows = GridCells.Fixed(2),
    ) {
        items(icons, key = { it.image }) { icon ->
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        onClick(icon)
                    }
                    .background(
                        color = if (selectedIcon == icon.image) selectedColor else normalColor
                    )
                    .padding(8.dp),
            ) {
                Icon(
                    modifier = iconModifier.size(
                        50.dp
                    ),
                    painter = painterResource(
                        icon.image
                    ),
                    contentDescription = icon.description,
                    tint = Color.Unspecified
                )
            }
        }
    }
}