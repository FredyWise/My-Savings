package com.fredy.mysavings.Feature.Presentation.NavigationComponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fredy.mysavings.R
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation.NavigationRoute


@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier.padding(
            20.dp
        ),
    ) {
        Icon(
            modifier = Modifier.size(60.dp),
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "App Icon",
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                color = textColor,
                fontSize = 25.sp
            )
            Text(
                text = stringResource(id = R.string.version),
                color = textColor,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun DrawerBody(
    items: List<NavigationRoute>,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (NavigationRoute) -> Unit,
    additionalItem: @Composable () -> Unit = { },
) {
    LazyColumn(modifier = modifier) {
        items(items, key = { it.route }) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp),
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription,
                    tint = textColor
                )
                Spacer(
                    modifier = Modifier.width(
                        16.dp
                    )
                )
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            additionalItem()
        }
    }
}