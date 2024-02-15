package com.fredy.mysavings.ui.Screens.ZCommonComponent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatDay
import com.fredy.mysavings.Util.formatTime
import com.fredy.mysavings.ViewModels.RecordMap

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    icon: Int,
    iconDescription: String,
    itemName: String,
    itemInfo: String,
    resource: Resource<List<RecordMap>>,
    onEmptyMessageClick: () -> Unit = {},
    onNavigationIconClick: () -> Unit,
    content: @Composable () -> Unit = {},
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
        SimpleEntityItem(
            modifier = Modifier.padding(8.dp),
            icon = icon,
            iconModifier = Modifier
                .size(
                    55.dp
                )
                .clip(
                    shape = MaterialTheme.shapes.small
                ),
            iconDescription = iconDescription
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(
                    vertical = 3.dp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = itemInfo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(
                    vertical = 3.dp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
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
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(text = "Total of: " + recordMaps.sumOf { it.records.size } + " records")
            }
            content()
            LazyColumn(
                modifier = modifier
                    .fillMaxHeight()
                    .padding(
                        bottom = 16.dp, end = 8.dp
                    ),
            ) {
                recordMaps.forEach { trueRecordMap ->
                    stickyHeader {
                        CustomStickyHeader(
                            modifier = Modifier.background(
                                backgroundColor
                            ),
                            title = formatDay(
                                trueRecordMap.recordDate
                            ),
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(
                        trueRecordMap.records,
                        key = { it.record.recordId }) { item ->
                        Divider(
                            modifier = Modifier.height(
                                0.3.dp
                            ),
                            color = onBackgroundColor.copy(
                                alpha = 0.4f
                            )
                        )
                        SimpleEntityItem(
                            modifier = Modifier.padding(
                                vertical = 4.dp
                            ),
                            iconModifier = Modifier
                                .size(
                                    40.dp
                                )
                                .clip(
                                    shape = CircleShape
                                ),
                            icon = item.toCategory.categoryIcon,
                            iconDescription = item.fromAccount.accountIconDescription,
                            endContent = {
                                Text(
                                    text = formatTime(
                                        item.record.recordDateTime.toLocalTime()
                                    ),
                                    color = onBackgroundColor,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                        ) {
                            Text(
                                text = item.toCategory.categoryName,
                                color = onBackgroundColor,
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1
                            )
                            Text(
                                text = formatBalanceAmount(
                                    item.record.recordAmount,
                                    item.record.recordCurrency,
                                    true
                                ),
                                color = onBackgroundColor,
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1
                            )
                        }

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
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    title: String,
    onNavigationIconClick: () -> Unit,
    actionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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
        content()
    }
}