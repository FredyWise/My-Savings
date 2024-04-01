package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.ToggleableInfo
import com.fredy.mysavings.Util.currencyCodes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    SimpleButton(
        modifier = modifier
            .padding(top = 16.dp)
            .clip(
                MaterialTheme.shapes.medium
            )
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

@Composable
fun CheckBoxes(
    modifier: Modifier = Modifier,
    list: List<String>,
    selectedCheckbox: List<String>,
    enableCheckAllBox: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.onSurface
): List<String> {
    val checkboxes = remember {
        list.map {
            ToggleableInfo(
                isChecked = selectedCheckbox.contains(it), text = it
            )
        }.toMutableStateList()
    }

    if (enableCheckAllBox) {
        var triState by remember {
            mutableStateOf(ToggleableState.Off)
        }
        triState = if (checkboxes.all { it.isChecked }) ToggleableState.On else ToggleableState.Off
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
                        if (isChecked) {
                            ToggleableState.On
                        } else {
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


@Composable
fun Switch(
    modifier: Modifier = Modifier,
    switchState: Boolean = false,
    leftIcon: ImageVector,
    rightIcon: ImageVector,
    size: Dp = 150.dp,
    iconSize: Dp = size / 3,
    padding: Dp = 10.dp,
    borderWidth: Dp = 1.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
    onClick: () -> Unit
) {
    val offset by animateDpAsState(
        targetValue = if (switchState) 0.dp else size,
        animationSpec = animationSpec, label = ""
    )

    Box(modifier = modifier
        .width(size * 2)
        .height(size)
        .clip(shape = parentShape)
        .clickable { onClick() }
        .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = parentShape
                )
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = leftIcon,
                    contentDescription = "Theme Icon",
                    tint = if (switchState) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = rightIcon,
                    contentDescription = "Theme Icon",
                    tint = if (switchState) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

@Composable
fun ThemeSwitcher(
    darkTheme: Boolean = false,
    size: Dp = 150.dp,
    iconSize: Dp = size / 3,
    padding: Dp = 10.dp,
    borderWidth: Dp = 1.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
    onClick: () -> Unit
) {
    val offset by animateDpAsState(
        targetValue = if (darkTheme) 0.dp else size,
        animationSpec = animationSpec
    )

    Box(modifier = Modifier
        .width(size * 2)
        .height(size)
        .clip(shape = parentShape)
        .clickable { onClick() }
        .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = parentShape
                )
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Nightlight,
                    contentDescription = "Theme Icon",
                    tint = if (darkTheme) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Theme Icon",
                    tint = if (darkTheme) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    delayTime: Long = 500L,
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
    val scope = rememberCoroutineScope()
    fun debounce(query: String, timeMillis:Long = delayTime) {
        scope.launch {
            delay(timeMillis)
            if (query == selectedText) {
                filteredData = currencyCodes.filter { data ->
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
            value = selectedText,
            singleLine = true,
            onValueChange = {
                selectedText = it
                debounce(it)
            },
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