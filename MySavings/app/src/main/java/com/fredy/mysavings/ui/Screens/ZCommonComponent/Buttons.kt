package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import com.fredy.mysavings.Util.ToggleableInfo
import com.fredy.mysavings.Util.currencyCodes


@Composable
fun CheckBoxes(
    modifier: Modifier = Modifier,
    list: List<String>,
    enableCheckAllBox: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.onSurface
): List<String> {
    val checkboxes = remember {
        list.map {
            ToggleableInfo(
                isChecked = true, text = it
            )
        }.toMutableStateList()
    }

    if (enableCheckAllBox) {
        var triState by remember {
            mutableStateOf(ToggleableState.On)
        }
        val toggleTriState = {
            triState = when (triState) {
                ToggleableState.Indeterminate -> ToggleableState.On
                ToggleableState.Off -> ToggleableState.On
                ToggleableState.On -> ToggleableState.Off
            }
            checkboxes.indices.forEach { index ->
                checkboxes[index] = checkboxes[index].copy(
                    isChecked = triState == ToggleableState.On
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    toggleTriState()
                },
        ) {
            TriStateCheckbox(
                state = triState,
                onClick = toggleTriState
            )
            Text(
                text = "Select All",
                color = textColor
            )
        }
    }
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3)
    ) {
        itemsIndexed(checkboxes) { index, info ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        checkboxes[index] = info.copy(
                            isChecked = !info.isChecked
                        )
                    },
            ) {
                Checkbox(
                    checked = info.isChecked,
                    onCheckedChange = { isChecked ->
                        if (isChecked){
                            ToggleableState.On
                        }else{
                            ToggleableState.Off
                        }
                        checkboxes[index] = info.copy(
                            isChecked = isChecked
                        )
                    },
                )
                Text(
                    text = info.text,
                    color = textColor
                )
            }
        }
    }
    return checkboxes.filter { it.isChecked }.map { it.text }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    modifier: Modifier = Modifier,
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    selectedText: String,
    onClick: (String) -> Unit
) {
    var selectedText by remember {
        mutableStateOf(
            selectedText
        )
    }
    var expanded by remember {
        mutableStateOf(
            false
        )
    }
    var filteredData by remember {
        mutableStateOf(currencyCodes)
    }

    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown

    ExposedDropdownMenuBox(modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = selectedText,
            singleLine = true,
            onValueChange = {
                selectedText = it
                expanded = true
                filteredData = currencyCodes.filter { data ->
                    data.contains(
                        it, ignoreCase = true
                    )
                }.sorted()
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = textFieldColors,
            trailingIcon = {
                Icon(
                    icon,
                    "contentDescription",
                )
            },
        )
        ExposedDropdownMenu(
            modifier = Modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            filteredData.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        selectedText = label
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