package com.fredy.budget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.domain.model.Budget
import com.fredy.domain.modelUI.ActionWithName
import com.fredy.ui.components.list.AdvancedEntityItem

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    itemBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    itemTextColor: Color = MaterialTheme.colorScheme.onSurface,
    budgets: List<Budget>,
    topItem: @Composable () -> Unit = {},
    onEntityClick: () -> Unit,
) {
    LazyColumn(modifier = modifier) {
        item {
            topItem()
        }
        items(budgets) {budget ->
            Box(modifier = Modifier) {
                AdvancedEntityItem(
                    modifier = Modifier
                        .padding(
                            vertical = 4.dp
                        )
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            width = 3.dp / 2,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.medium
                        )
                        .background(
                            itemBackgroundColor
                        )
                        .clickable {
                            onEntityClick()

                        },
                    icon = budget.category.categoryIcon,
                    iconModifier = Modifier
                        .size(
                            40.dp
                        )
                        .clip(
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    iconDescription = budget.category.categoryIconDescription,
                    menuItems = listOf(
                        ActionWithName(
                            name = "Delete Budget",
                            action = {

                            },
                        ), ActionWithName(
                            name = "Edit Budget",
                            action = {

                            },
                        )
                    )
                ) {
                    Text(
                        text = budget.category.categoryName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = itemTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

        }
    }
}
