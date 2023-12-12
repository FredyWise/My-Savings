package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Enum.FilterType

@Composable
fun FilterDialog(
    modifier: Modifier = Modifier,
    title: String,
    selectedName: String,
    checkboxList: List<String> = emptyList(),
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
        }
    ) {
        CustomStickyHeader(
            title = "Filter",
            textStyle = MaterialTheme.typography.titleLarge
        )
        FilterType.values().forEach { item ->
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
                        .height(23.dp)
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
        if (checkboxList.isNotEmpty()) {
            onSelectCheckbox(CheckBoxes(list = checkboxList))
        }
    }
}



