package com.fredy.ui.components.button

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fredy.theme.components.SimpleButton

@Composable
fun SettingButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    SimpleButton(
        modifier = modifier
            .padding(top = 16.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp),
        onClick = {
            onClick()
        },
        title = text,
        titleStyle = MaterialTheme.typography.titleLarge.copy(
            MaterialTheme.colorScheme.onBackground
        )
    )
}