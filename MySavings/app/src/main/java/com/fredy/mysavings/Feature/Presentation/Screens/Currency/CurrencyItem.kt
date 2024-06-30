package com.fredy.mysavings.Feature.Presentation.Screens.Currency

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.AsyncImageHandler

@Composable
fun CurrencyItem(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    contentDescription: String = "",
    name: String,
    code: String = "",
    leadingComponent: @Composable () -> Unit,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImageHandler(
            modifier = Modifier.size(50.dp),
            imageUrl = imageUrl,
            imageScale = ContentScale.Fit,
            contentDescription = contentDescription,
            imageVector = Icons.Default.CurrencyExchange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .weight(1f, fill = false),
                maxLines = Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
            )
            if (code.isNotEmpty()) {
                Text(
                    text = " | $code",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onBackgroundColor,
                    modifier = Modifier
                        .padding(vertical = 3.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        leadingComponent()
    }
}