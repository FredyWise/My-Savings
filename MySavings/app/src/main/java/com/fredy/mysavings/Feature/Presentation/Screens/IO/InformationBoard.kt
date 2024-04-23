package com.fredy.mysavings.Feature.Presentation.Screens.IO

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.Feature.Presentation.ViewModels.DBInfo

@Composable
fun InformationBoard(
    modifier: Modifier = Modifier,
    dbInfo: List<DBInfo>,
    textColor: Color,
    title: (Int) -> String
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(dbInfo, key = { index, item -> index }) { index, item ->
            val key = "$index$item".hashCode()
            val isVisible = remember(key) {
                MutableTransitionState(
                    false
                ).apply { targetState = true }
            }
            AnimatedVisibility(
                visibleState = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                ) {
                    CustomStickyHeader(
                        topPadding = 10.dp,
                        textStyle = MaterialTheme.typography.titleLarge,
                        title = title(index)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(Modifier.padding(start = 8.dp)) {
                        Text("Total Records: ${item.sumOfRecords}", color = textColor)
                        Text("Total Accounts: ${item.sumOfAccounts}", color = textColor)
                        Text("Total Categories: ${item.sumOfCategories}", color = textColor)
                        Text("Total Expenses: ${item.sumOfExpense}", color = textColor)
                        Text("Total Income: ${item.sumOfIncome}", color = textColor)
                        Text("Total Transfers: ${item.sumOfTransfer}", color = textColor)
                    }
                }
            }
        }
    }
}