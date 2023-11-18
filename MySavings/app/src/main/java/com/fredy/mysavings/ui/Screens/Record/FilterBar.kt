package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.RoomDatabase.Enum.FilterType
import com.fredy.mysavings.Data.RoomDatabase.Event.RecordsEvent
import com.fredy.mysavings.ui.Screens.ActionWithName

@Composable
fun DisplayBar(
    modifier: Modifier = Modifier,
    onEvent: (RecordsEvent) -> Unit,
    selectedDate: String,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface
            )
            .padding(horizontal =4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable {

                }
                .padding(4.dp),
            imageVector = Icons.Default.Sort,
            contentDescription = "",
            tint = tint,
        )
        Icon(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable {

                }
                .padding(4.dp),
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "",
            tint = tint,
        )
        Text(
            text = selectedDate,
            color = tint,
        )
        Icon(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable {

                }
                .padding(4.dp),
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "",
            tint = tint,
        )
        Icon(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable {

                }
                .padding(4.dp),
            imageVector = Icons.Default.FilterList,
            contentDescription = "",
            tint = tint,
        )
    }

}

@Composable
fun FilterDialog(

    menuItems: List<ActionWithName>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterType.values().forEach { filterType ->
            Row(
                modifier = Modifier.clickable {
//                    onEvent(
//                        RecordsEvent.SortRecord(
//                            filterType
//                        )
//                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
//                RadioButton(selected = displayOption == filterType,
//                    onClick = {
//                        onEvent(
//                            RecordsEvent.SortRecord(
//                                filterType
//                            )
//                        )
//                    })
                Text(text = filterType.name)
            }
        }
    }
}