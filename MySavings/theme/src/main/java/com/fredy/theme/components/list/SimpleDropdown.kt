package com.fredy.theme.components.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdown(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    textFieldEnabled: Boolean = true,
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        disabledIndicatorColor = MaterialTheme.colorScheme.onSurface
    ),
    delayTime: Long = 500L,
    textFieldShape: Shape = TextFieldDefaults.shape,
    list: List<String>,
    selectedText: String,
    onClick: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedTextState by remember(selectedText) {
        mutableStateOf(selectedText)
    }
    var filteredData by remember(list) {
        mutableStateOf(list)
    }
    val scope = rememberCoroutineScope()
    fun debounce(query: String, timeMillis: Long = delayTime) {
        scope.launch {
            delay(timeMillis)
            if (query == selectedTextState) {
                filteredData = list.filter { data ->
                    data.contains(query.replace(" ", ""), ignoreCase = true)
                }.sorted()
                expanded = true
            }
        }
    }

    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { },
    ) {
        TextField(
            value = selectedTextState,
            singleLine = true,
            enabled = textFieldEnabled,
            onValueChange = {
                selectedTextState = it
                debounce(it)
            },
            shape = textFieldShape,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = textFieldColors,
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            expanded = !expanded
                        }
                        .padding(8.dp),
                    imageVector = icon,
                    contentDescription = "contentDescription",
                )
            },
        )
        ExposedDropdownMenu(
            modifier = menuModifier
                .heightIn(0.dp, 240.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            filteredData.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        selectedTextState = label
                        onClick(label)
                    },
                    text = {
                        Text(
                            text = label,
                        )
                    },
                )

            }
        }
    }
}

