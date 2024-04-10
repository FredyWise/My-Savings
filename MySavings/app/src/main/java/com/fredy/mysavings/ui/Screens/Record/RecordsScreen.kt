package com.fredy.mysavings.ui.Screens.Record

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
) {
    val key = state.resourceData.recordMapsResource.hashCode()
    val isVisible = remember(key) {
        MutableTransitionState(
            false
        ).apply { targetState = true }
    }
    AnimatedVisibility(
        modifier = modifier,
        visibleState = isVisible,
        enter =  fadeIn(),
        exit = fadeOut()
    ) {
        state.resourceData.recordMapsResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "There is no record on this date yet",
                isNullOrEmpty = { it.isNullOrEmpty() },
                errorMessage = resource.message ?: "",
                onMessageClick = {
                    rootNavController.navigate(
                        "${NavigationRoute.Add.route}?bookId=${state.filterState.currentBook?.bookId}"
                    )
                    isVisible.targetState = false
                },
            ) { data ->
                RecordBody(
                    bookMaps = data,
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }

}

