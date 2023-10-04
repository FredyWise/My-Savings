package com.fredy.mysavings.ui.component

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.FormatAmount
import com.fredy.mysavings.Data.User.defaultColors
import com.fredy.mysavings.Data.User.extractProportions

@Composable
fun BasicButton(
    modifier: Modifier = Modifier,
    buttonShape: Shape = MaterialTheme.shapes.small,
    selected: Boolean = false,
    selectedColor: Color = Color.Unspecified,
    buttonBackgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    spacing: Dp = 8.dp,
    onClick: () -> Unit,
    btnElement: @Composable () -> Unit
) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected && buttonBackgroundColor != Color.Unspecified) selectedColor else buttonBackgroundColor,
                buttonShape
            )
            .clickable {
                onClick()
            }
            .then(modifier)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                spacing
            ),
            modifier = Modifier.border(
                2.dp, borderColor, buttonShape
            )
        ) {
            btnElement()
        }
    }
}

