package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Data.Database.Model.Book
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.DefaultData
import com.fredy.mysavings.ViewModels.BookMap

@Composable
fun RecordHeader(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    items: List<BookMap>,
    onBookClicked: (Book) -> Unit,
    onAddBook: () -> Unit,
) {
    LazyRow(modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        items(items) { item ->
            Column(
                modifier = Modifier
                    .clickable {
                        onBookClicked(item.book)
                    }
                    .padding(
                        vertical = 4.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .size(
                            55.dp
                        )
                        .clip(
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    painter = painterResource(
                        id = DefaultData.savingsIcons[item.book.bookIconDescription]?.image
                            ?: item.book.bookIcon
                    ),
                    contentDescription = item.book.bookIconDescription,
                    tint = Color.Unspecified
                )
                Text(
                    text = item.book.bookName,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        item {
            Column(
                modifier = Modifier
                    .clickable {
                        onAddBook()
                    }
                    .padding(
                        vertical = 4.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .size(
                            55.dp
                        )
                        .clip(
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    painter = painterResource(
                        R.drawable.ic_add_foreground
                    ),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
                Text(
                    text = "Add New Book",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                )
            }
        }
    }
}