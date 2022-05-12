package io.taptap.uikit.fab

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.taptap.uikit.AnimatingFabContent

val BOTTOM_BAR_VERTICAL_PADDING = 12.dp
val BOTTOM_BAR_HEIGHT = 56.dp
val MINI_FAB_HEIGHT = 42.dp
val FAB_HEIGHT = 52.dp
val BOTTOM_BAR_MARGIN = BOTTOM_BAR_HEIGHT + BOTTOM_BAR_VERTICAL_PADDING * 2
val BOTTOM_BAR_MARGIN_WITH_FAB = BOTTOM_BAR_MARGIN + FAB_HEIGHT

//ver2
@Composable
fun Fab(
    enlarged: Boolean,
    iconRes: Int,
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier,
    onFabClicked: () -> Unit
) {
    key(text) { // Prevent multiple invocations to execute during composition
        FloatingActionButton(
            containerColor = containerColor,
            contentColor = contentColor,
            onClick = onFabClicked,
            modifier = modifier
                .navigationBarsPadding()
                .padding(bottom = BOTTOM_BAR_MARGIN, start = 16.dp, end = 16.dp)
                .height(FAB_HEIGHT)
                .widthIn(min = FAB_HEIGHT)
        ) {
            AnimatingFabContent(
                icon = {
                    Icon(
                        painterResource(id = iconRes),
                        contentDescription = text
                    )
                },
                text = {
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = text
                    )
                },
                enlarged = enlarged
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MultiFloatingActionButton(
    modifier: Modifier = Modifier,
    items: List<MultiFabItem>,
    fabState: MutableState<MultiFabState> = rememberMultiFabState(),
    fabIcon: FabIcon,
    enlarged: Boolean,
    fabOption: FabOption = FabOption(),
    stateChanged: (fabState: MultiFabState) -> Unit = {}
) {
    key(fabIcon.description) {
        val rotation by animateFloatAsState(
            if (fabState.value == MultiFabState.Expand) {
                fabIcon.iconRotate ?: 0f
            } else {
                0f
            }
        )

        Column(
            modifier = modifier
                .wrapContentSize()
                .navigationBarsPadding()
                .padding(bottom = BOTTOM_BAR_MARGIN, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            AnimatedVisibility(
                visible = fabState.value.isExpanded(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = items, key = { it.id }) { item ->
                        MiniFabItem(
                            item = item,
                            fabOption = fabOption
                        )
                    }

                    item {} // for Spacing
                }
            }
            FloatingActionButton(
                onClick = {
                    fabState.value = fabState.value.toggleValue()
                    stateChanged(fabState.value)
                },
                containerColor = fabOption.containerColor,
                contentColor = fabOption.contentColor,
                modifier = modifier
                    .height(FAB_HEIGHT)
                    .widthIn(min = FAB_HEIGHT)
            ) {
                AnimatingFabContent(
                    icon = {
                        Icon(
                            painterResource(id = fabIcon.iconRes),
                            contentDescription = fabIcon.description,
                            modifier = Modifier.rotate(rotation)
                        )
                    },
                    text = {
                        Text(
                            style = MaterialTheme.typography.headlineLarge,
                            text = fabIcon.description ?: ""
                        )
                    },
                    enlarged = enlarged
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MiniFabItem(
    item: MultiFabItem,
    fabOption: FabOption
) {
    FloatingActionButton(
        modifier = Modifier
            .wrapContentWidth()
            .height(MINI_FAB_HEIGHT)
            .widthIn(min = MINI_FAB_HEIGHT),
        onClick = item.onClicked,
        containerColor = fabOption.containerColor,
        contentColor = fabOption.contentColor
    ) {
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text = item.label,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        )
    }
}

/**
 * @param id Cannot be duplicated with the [id] value of another [MultiFabItem].
 */
data class MultiFabItem(
    val id: Int,
    @DrawableRes val iconRes: Int = 0,
    val label: String = "",
    val onClicked: () -> Unit
)
