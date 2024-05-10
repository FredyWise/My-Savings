package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Domain.Model.RecordMap
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Presentation.Screens.Record.MainScreen.RecordEntityItem
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.Feature.Presentation.Util.formatDateDay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordList(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    recordMaps: List<RecordMap>?,
    onItemClick: (TrueRecord) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        recordMaps?.forEach { trueRecordMap ->
            stickyHeader {
                CustomStickyHeader(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.background
                    ),
                    title = formatDateDay(
                        trueRecordMap.recordDate
                    ),
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            items(trueRecordMap.records, key = { it.record.recordId }) { item ->
                Divider(
                    modifier = Modifier.height(0.3.dp),
                    color = onBackgroundColor.copy(
                        alpha = 0.4f
                    )
                )
                RecordEntityItem(
                    backgroundColor = backgroundColor,
                    onBackgroundColor = onBackgroundColor,
                    item = item,
                    onItemClick = {
                        onItemClick(item)
                    },
                )
            }
        }

    }

}