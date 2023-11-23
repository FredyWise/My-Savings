package com.fredy.mysavings.ui.NavigationComponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fredy.mysavings.R
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute


@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(
            20.dp
        ), horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 25.sp
        )
        Text(
            text = stringResource(id = R.string.version),
            fontSize = 15.sp
        )
    }
}

@Composable
fun DrawerBody(
    items: List<NavigationRoute>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (NavigationRoute) -> Unit,
    additionalItem: @Composable () -> Unit = {  },
) {
    LazyColumn(modifier = modifier) {
        items(items) { item ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onItemClick(item)
                }
                .padding(16.dp)) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription
                )
                Spacer(
                    modifier = Modifier.width(
                        16.dp
                    )
                )
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            additionalItem()
        }
    }
}