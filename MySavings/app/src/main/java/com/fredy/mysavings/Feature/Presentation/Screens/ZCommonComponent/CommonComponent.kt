package com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fredy.mysavings.Feature.Presentation.Util.ActionWithName
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Util.SavingsIcon
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun <T> ResourceHandler(
    modifier: Modifier = Modifier,
    resource: Resource<T>,
    isNullOrEmpty: (T?) -> Boolean,
    errorMessage: String = "",
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

    fun debounce(resource: Resource<T>) {
        job?.cancel()
        job = scope.launch {
            if (resource is Resource.Success) {
                showCircularProgressIndicator = false
                delay(500L)
                showEmptyMessage = true
            }
            if (resource is Resource.Loading) {
                showEmptyMessage = false
                delay(1000L)
                showCircularProgressIndicator = true
            }
        }
    }

    if (resource is Resource.Loading || resource is Resource.Success) {
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
                        errorMessage,
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

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    searchText: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search",
    isSearching: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit = {},
    searchBody: @Composable () -> Unit = {},
) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = searchText,
                onValueChange = { onValueChange(it) },
                modifier = Modifier
                    .weight(1f)
                    .clip(
                        CircleShape
                    ),
                placeholder = { Text(text = placeholder) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor
                ),
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = true
            )
            trailingContent()
        }
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }
        } else {
            searchBody()
        }
    }
}


@Composable
fun AdvancedEntityItem(
    modifier: Modifier = Modifier,
    icon: Int,
    iconModifier: Modifier = Modifier,
    iconDescription: String,
    menuItems: List<ActionWithName>,
    content: @Composable () -> Unit,
) {
    var isShowMenu by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    SimpleEntityItem(
        modifier = modifier
            .indication(
                interactionSource,
                LocalIndication.current
            )
//            .pointerInput(true) {
//                detectTapGestures(onLongPress = {
//                    isShowMenu = true
//                }, onPress = {
//                    val press = PressInteraction.Press(
//                        it
//                    )
//                    interactionSource.emit(
//                        press
//                    )
//                    tryAwaitRelease()
//                    interactionSource.emit(
//                        PressInteraction.Release(
//                            press
//                        )
//                    )
//                })
//            }
            .padding(
                8.dp
            ),
        icon = icon,
        iconModifier = iconModifier,
        iconDescription = iconDescription,
        content = content,
        endContent = {
            Icon(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        isShowMenu = true
                    }
                    .padding(4.dp),
                imageVector = Icons.Default.MoreVert,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
            SimpleDropDownMenu(pressOffset = pressOffset,
                menuItems = menuItems,
                isShowMenu = isShowMenu,
                onClose = { isShowMenu = false })
        },
    )
}


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


@Composable
fun TypeRadioButton(
    modifier: Modifier = Modifier,
    onSelectedColor: Color = MaterialTheme.colorScheme.onBackground,
    selectedName: String,
    radioButtons: List<ActionWithName>,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    barHeight: Dp = 35.dp,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup()
            .height(
                barHeight
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        radioButtons.forEachIndexed { index, button ->
            val durationMillis = if (selectedName == button.name) 100 else 50
            val animSpec = remember {
                tween<Color>(
                    durationMillis = durationMillis,
                    easing = LinearEasing,
                    delayMillis = 100
                )
            }
            val selectedColor by animateColorAsState(
                targetValue = if (selectedName == button.name) onSelectedColor else onSelectedColor.copy(
                    alpha = 0.5f
                ),
                animationSpec = animSpec, label = ""
            )
            if (index > 0 && index < radioButtons.size) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(
                            2.dp
                        ),
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.5f
                    )
                )
            }
            Row(
                modifier = Modifier
                    .animateContentSize()
                    .selectable(
                        selected = selectedName == button.name,
                        onClick = button.action,
                        role = Role.RadioButton,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = Dp.Unspecified,
                            color = Color.Unspecified
                        )
                    )
                    .padding(vertical = 8.dp)
                    .weight(
                        1f
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedName == button.name) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "",
                        tint = selectedColor
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = button.name,
                    style = textStyle,
                    color = selectedColor
                )
            }
        }
    }
}


@Composable
fun ChooseIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    normalColor: Color = Color.Unspecified,
    onClick: (SavingsIcon) -> Unit,
    selectedIcon: Int,
    icons: List<SavingsIcon>,
) {
    LazyHorizontalGrid(
        modifier = modifier
            .height(132.dp)
            .clip(
                shape = MaterialTheme.shapes.medium
            )
            .background(
                MaterialTheme.colorScheme.background
            ),
        rows = GridCells.Fixed(2),
    ) {
        items(icons, key = { it.image }) { icon ->
            Box(
                modifier = Modifier
                    .clip(
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        onClick(icon)
                    }
                    .background(
                        color = if (selectedIcon == icon.image) selectedColor else normalColor
                    )
                    .padding(8.dp),
            ) {
                Icon(
                    modifier = iconModifier.size(
                        50.dp
                    ),
                    painter = painterResource(
                        icon.image
                    ),
                    contentDescription = icon.description,
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun AsyncImageHandler(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    imageScale: ContentScale = ContentScale.Crop,
    contentDescription: String = "",
    imageVector: ImageVector,
    imageVectorColor: Color = MaterialTheme.colorScheme.onSurface,

    ) {
    if (!imageUrl.isNullOrEmpty() && !imageUrl.contains("null", true)) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = imageScale
        )
    } else {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = imageVectorColor,
            modifier = modifier,
        )
    }
}


