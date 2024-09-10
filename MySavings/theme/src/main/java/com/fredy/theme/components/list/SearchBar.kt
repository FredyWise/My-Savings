package com.fredy.theme.components.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    searchText: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search",
    isSearching: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit = {},
    searchBody: @Composable () -> Unit = {},
) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = searchText,
                onValueChange = { onValueChange(it) },
                modifier = Modifier
                    .weight(1f)
                    .clip(
                        CircleShape
                    )
                    .border(3.dp / 2, MaterialTheme.colorScheme.secondary, CircleShape),
                placeholder = { Text(text = placeholder) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor
                ),
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = true
            )
            trailingContent()
        }
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }
        } else {
            searchBody()
        }
    }
}