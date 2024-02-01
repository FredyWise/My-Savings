package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Enum.FilterType

@Composable
fun FilterDialog(
    modifier: Modifier = Modifier,
    title: String,
    selectedName: String,
    checkboxList: List<String> = emptyList(),
    selectedCheckbox: List<String> = emptyList(),
    sortType: Boolean = false,
    carryOn: Boolean = true,
    showTotal: Boolean = true,
    onShortChange: () -> Unit,
    onCarryOnChange: () -> Unit,
    onShowTotalChange: () -> Unit,
    onDismissRequest: () -> Unit,
    onSelectItem: (FilterType) -> Unit,
    onSelectCheckbox: (List<String>) -> Unit,
) {
    SimpleAlertDialog(
        modifier = modifier,
        title = title,
        onDismissRequest = {
            onDismissRequest()
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .clip(
                            CircleShape
                        )
                        .clickable { onDismissRequest() }
                        .padding(
                            8.dp
                        ),
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(
                        0.5f
                    ),
                    text = "Sort by: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(
                    modifier = Modifier.weight(
                        0.5f
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (sortType) "Ascending" else "Descending",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Switch(
                        switchState = sortType,
                        leftIcon = Icons.Default.ArrowUpward,
                        rightIcon = Icons.Default.ArrowDownward,
                        size = 28.dp,
                        padding = 3.dp,
                        onClick = {
                            onShortChange()
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(
                        0.5f
                    ),
                    text = "Carry On: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(
                    modifier = Modifier.weight(
                        0.5f
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Switch(
                        switchState = !carryOn,
                        leftIcon = Icons.Default.Close,
                        rightIcon = Icons.Default.Check,
                        size = 28.dp,
                        padding = 3.dp,
                        onClick = {
                            onCarryOnChange()
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(
                        0.5f
                    ),
                    text = "Show Total: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(
                    modifier = Modifier.weight(
                        0.5f
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Switch(
                        switchState = !showTotal,
                        leftIcon = Icons.Default.Close,
                        rightIcon = Icons.Default.Check,
                        size = 28.dp,
                        padding = 3.dp,
                        onClick = {
                            onShowTotalChange()
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Column(modifier = Modifier) {
            CustomStickyHeader(
                title = "Filter",
                topPadding = 0.dp,
                textStyle = MaterialTheme.typography.titleLarge
            )
            FilterType.values().forEach { item ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp
                        )
                        .clickable {
                            onSelectItem(item)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        modifier = Modifier
                            .height(
                                23.dp
                            )
                            .padding(
                                end = 8.dp
                            ),
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (selectedName == item.name) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
        if (checkboxList.isNotEmpty()) {
            onSelectCheckbox(
                CheckBoxes(
                    list = checkboxList,
                    selectedCheckbox = selectedCheckbox
                )
            )
        }
    }
}



