package com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Util.RecordTypeColor
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.formatDateDay
import com.fredy.mysavings.Feature.Domain.Model.RecordMap

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    resource: Resource<List<RecordMap>>,
    onEmptyMessageClick: () -> Unit = {},
    onNavigationIconClick: () -> Unit,
    additionalAppbar: @Composable () -> Unit,
    content: @Composable (item: TrueRecord, itemColor: Color, balanceColor:Color) -> Unit,
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
            errorMessage = resource.message ?: "",
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
                    Row(modifier = Modifier
                        .padding(horizontal = 8.dp)) {
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
                        key = { it.record.recordId },) { item ->
                        Divider(
                            modifier = Modifier.height(
                                0.3.dp
                            ),
                            color = onBackgroundColor.copy(
                                alpha = 0.4f
                            )
                        )
                        content(item, onBackgroundColor, RecordTypeColor(recordType = item.record.recordType))
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    onNavigationIconClick: () -> Unit,
    actionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
                Text(text = title)
            },
            actions = { actionButton() },
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .padding(
                            horizontal = 4.dp
                        )
                        .clip(CircleShape)
                        .clickable {
                            onNavigationIconClick()
                        }
                        .padding(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Close",
                        tint = iconColor
                    )
                }
            },
        )
        Column(modifier = contentModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally){
            content()
        }
    }
}