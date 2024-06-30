package com.fredy.mysavings.Feature.Presentation.Util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fredy.mysavings.R


object Strings {
    var hello = R.string.sort;

    @Composable
    fun getString(id: Int): String {
        return stringResource(id)
    }

}