package com.fredy.addrecord.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fredy.addrecord.R
import com.fredy.domain.util.SavingsIcons.CheckIcon
import com.fredy.domain.util.SavingsIcons.CloseIcon
import com.fredy.ui.components.button.SimpleButton

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
            image = CloseIcon.image,
            imageColor = onBackground,
            title = "CANCEL",
            titleStyle = MaterialTheme.typography.titleMedium.copy(
                onBackground
            ),
        )
        SimpleButton(
            onClick = onSaveClick,
            image = CheckIcon.image,
            imageColor = onBackground,
            title = "SAVE",
            titleStyle = MaterialTheme.typography.titleMedium.copy(
                onBackground
            ),
        )
    }
}