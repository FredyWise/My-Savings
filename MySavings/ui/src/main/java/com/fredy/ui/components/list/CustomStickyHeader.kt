package com.fredy.ui.components.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun CustomStickyHeader(
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    textColor: Color = MaterialTheme.colorScheme.primary,
    topPadding: Dp = 28.dp,
    bottomPadding: Dp = 4.dp,
    useDivider: Boolean = true,
    title: String
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = topPadding,
                    start = 8.dp,
                    bottom = bottomPadding,
                ),
        )
        if (useDivider) {
            Divider(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .height(
                        2.dp
                    ),
                color = textColor
            )
        }
    }
}