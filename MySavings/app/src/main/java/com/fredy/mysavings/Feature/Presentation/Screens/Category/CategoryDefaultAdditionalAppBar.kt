package com.fredy.mysavings.Feature.Presentation.Screens.Category

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleEntityItem
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryState

@Composable
fun CategoryDefaultAdditionalAppBar(
    state: CategoryState,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    SimpleEntityItem(
        modifier = Modifier.padding(8.dp),
        icon = state.category.categoryIcon,
        iconModifier = Modifier
            .size(
                55.dp
            )
            .clip(
                shape = MaterialTheme.shapes.small
            ),
        iconDescription = state.category.categoryIconDescription
    ) {
        Text(
            text = state.category.categoryName,
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
            text = "Category Type: " + state.category.categoryType.name,
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
}