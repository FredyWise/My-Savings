package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleButton
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.R

@Composable
fun AddConfirmationRow(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SimpleButton(
            onClick = onCancelClick,
            image = R.drawable.ic_close_foreground,
            imageColor = onBackground,
            title = "CANCEL",
            titleStyle = MaterialTheme.typography.titleMedium.copy(
                onBackground
            ),
        )
        SimpleButton(
            onClick = onSaveClick,
            image = R.drawable.ic_check_foreground,
            imageColor = onBackground,
            title = "SAVE",
            titleStyle = MaterialTheme.typography.titleMedium.copy(
                onBackground
            ),
        )
    }
}