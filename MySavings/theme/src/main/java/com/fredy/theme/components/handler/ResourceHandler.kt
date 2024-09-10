package com.fredy.theme.components.handler

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.util.resource.ResourceError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun <T> ResourceHandler(
    modifier: Modifier = Modifier,
    resource: Resource<T, ResourceError>,
    isNullOrEmpty: (T?) -> Boolean,
    errorMessage: String? = null,
    nullOrEmptyMessage: String = "",
    onMessageClick: () -> Unit,
    enterTransition: EnterTransition = fadeIn(),
    exitTransition: ExitTransition = fadeOut(),
    content: @Composable (T) -> Unit,
) {
    val key = resource.hashCode()
    val isVisible = remember(key) {
        MutableTransitionState(
            false
        ).apply { targetState = true }
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCircularProgressIndicator by remember {
        mutableStateOf(false)
    }
    var showEmptyMessage by remember {
        mutableStateOf(false)
    }
    var job: Job? = null

    fun debounce(resource: Resource<T,ResourceError>) {
        job?.cancel()
        job = scope.launch {
            showCircularProgressIndicator = false
            showEmptyMessage = false
            if (resource is Resource.Success) {
                delay(500L)
                showEmptyMessage = resource is Resource.Success
            }
            if (resource is Resource.Loading) {
                delay(1300L)
                showCircularProgressIndicator = resource is Resource.Loading
            }
        }
    }

    LaunchedEffect(key1 = resource.hashCode()) {
        debounce(resource)
    }
    AnimatedVisibility(
        visibleState = isVisible,
        enter = enterTransition,
        exit = exitTransition,
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.TopCenter
        ) {
            when (resource) {
                is Resource.Error -> {
                    Toast.makeText(
                        context,
                        errorMessage ?: resource.error.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (showCircularProgressIndicator) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(
                                    40.dp
                                ),
                                strokeWidth = 4.dp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                is Resource.Success -> {
                    resource.data.let {
                        if (!isNullOrEmpty(it)) {
                            content(it!!)
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (showEmptyMessage) {
                                    Text(
                                        text = nullOrEmptyMessage,
                                        modifier = Modifier
                                            .clip(
                                                MaterialTheme.shapes.medium
                                            )
                                            .clickable {
                                                onMessageClick()
                                                isVisible.targetState = false
                                            }
                                            .padding(
                                                20.dp
                                            ),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }

                            }
                        }
                    }
                }
            }

        }
    }

}