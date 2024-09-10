package com.fredy.theme.components.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.domain.model.RecordMap
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.theme.components.list.CustomStickyHeader
import com.fredy.theme.components.handler.ResourceHandler
import com.fredy.theme.util.RecordTypeColor
import com.fredy.theme.util.formatDateDay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    resource: Resource<List<RecordMap>, DataError.Local>,
    onEmptyMessageClick: () -> Unit = {},
    onNavigationIconClick: () -> Unit,
    additionalAppbar: @Composable () -> Unit,
    content: @Composable (item: TrueRecord, itemColor: Color, balanceColor: Color) -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                backgroundColor
            )
            .padding(top = 8.dp)
            .padding(
                horizontal = 8.dp
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(modifier = Modifier
                .padding(
                    horizontal = 8.dp
                )
                .clip(
                    shape = CircleShape
                )
                .clickable { onNavigationIconClick() }
                .size(
                    35.dp
                ),
                tint = onBackgroundColor,
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Close")
            Text(
                modifier = Modifier.padding(
                    vertical = 8.dp
                ),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        additionalAppbar()
        ResourceHandler(
            resource = resource,
            nullOrEmptyMessage = "You haven't made any Record using this account yet",
            isNullOrEmpty = { it.isNullOrEmpty() },
            onMessageClick = {
                onEmptyMessageClick()
                onNavigationIconClick()
            },
        ) { recordMaps ->
            LazyColumn(
                modifier = modifier
                    .fillMaxHeight()
                    .padding(
                        bottom = 16.dp, end = 8.dp
                    ),
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(text = "Total of: " + recordMaps.sumOf { it.records.size } + " records")
                    }
                }
                recordMaps.forEach { trueRecordMap ->
                    stickyHeader {
                        CustomStickyHeader(
                            modifier = Modifier.background(
                                backgroundColor
                            ),
                            title = formatDateDay(
                                trueRecordMap.recordDate
                            ),
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(
                        trueRecordMap.records,
                        key = { it.record.recordId },
                    ) { item ->
                        Divider(
                            modifier = Modifier.height(
                                0.3.dp
                            ),
                            color = onBackgroundColor.copy(
                                alpha = 0.4f
                            )
                        )
                        content(
                            item,
                            onBackgroundColor,
                            RecordTypeColor(recordType = item.record.recordType)
                        )
                    }
                }
            }
        }

    }
}