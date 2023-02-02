package io.taptap.stupidenglish.features.groups.ui

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.AverageText
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.GradientButton
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.StupidEnglishTopAppBar
import io.taptap.uikit.fab.BOTTOM_BAR_MARGIN
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListItemsModel
import io.taptap.uikit.group.GroupListModel
import io.taptap.uikit.group.GroupListTitleUI
import io.taptap.uikit.group.NoGroupItemUI
import io.taptap.uikit.group.getTitle
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import io.taptap.uikit.theme.getStupidLanguageBackgroundRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun GroupListScreen(
    context: Context,
    state: GroupListContract.State,
    effectFlow: Flow<GroupListContract.Effect>?,
    onEventSent: (event: GroupListContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: GroupListContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is GroupListContract.Effect.GetGroupsError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(effect.errorRes),
                        duration = SnackbarDuration.Short
                    )
                is GroupListContract.Effect.Navigation.BackToWordList ->
                    onNavigationRequested(effect)
                is GroupListContract.Effect.Navigation.ToGroupDetails ->
                    onNavigationRequested(effect)
                is GroupListContract.Effect.ShowRecover -> {
                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(R.string.stns_delete_message),
                        duration = SnackbarDuration.Short,
                        actionLabel = context.getString(R.string.stns_recover)
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> onEventSent(GroupListContract.Event.OnApplyDismiss)
                        SnackbarResult.ActionPerformed -> onEventSent(GroupListContract.Event.OnRecover)
                    }
                }
            }
        }?.collect()
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) {
        ContentScreen(
            state,
            onEventSent
        )
    }
}

@ExperimentalFoundationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ContentScreen(
    state: GroupListContract.State,
    onEventSent: (event: GroupListContract.Event) -> Unit
) {
    StupidLanguageBackgroundBox(
        topbar = {
            StupidEnglishTopAppBar(
                text = stringResource(id = R.string.grps_list_title),
                onNavigationClick = { onEventSent(GroupListContract.Event.OnBackClick) },
            )
        }
    ) {
        val listState = rememberLazyListState()

        GroupList(
            groupItems = state.groups,
            deletedGroupsIds = state.deletedGroupsIds,
            listState = listState,
            onEventSent = onEventSent
        )
        if (state.isLoading) {
            LoadingBar()
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun GroupList(
    groupItems: List<GroupListModel>,
    deletedGroupsIds: MutableList<Long>,
    listState: LazyListState,
    onEventSent: (event: GroupListContract.Event) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            top = WindowInsets.navigationBars.getTop(LocalDensity.current).dp,
            bottom = WindowInsets.navigationBars.getBottom(LocalDensity.current).dp + 12.dp + BOTTOM_BAR_MARGIN
        )
    ) {
        items(
            items = groupItems,
            key = { it.id }
        ) { item ->
            val dismissState = rememberDismissState()
            if (deletedGroupsIds.contains(item.id)) { //todo выделить в отдельный класс с возможностью удалять?
                if (dismissState.currentValue != DismissValue.Default) {
                    LaunchedEffect(Unit) {
                        dismissState.reset()
                        onEventSent(GroupListContract.Event.OnRecovered(item as GroupItemUI))
                    }
                }
            } else {
                if (item is GroupItemUI) {
                    if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                        onEventSent(GroupListContract.Event.OnDismiss(item))
                    }
                }
            }

            when (item) {
                is NoGroupItemUI -> NoRemovableGroupItemRow(
                    item = item,
                    modifier = Modifier.animateItemPlacement(),
                    onClicked = {
                        onEventSent(GroupListContract.Event.OnGroupClick(item))
                    },
                    onShareClicked = { group ->
                        onEventSent(GroupListContract.Event.OnShareClick(group))
                    }
                )
                is GroupListItemsModel -> GroupItemRow(
                    item = item,
                    dismissState = dismissState,
                    modifier = Modifier.animateItemPlacement(),
                    onClicked = {
                        onEventSent(GroupListContract.Event.OnGroupClick(item))
                    },
                    onShareClicked = { group ->
                        onEventSent(GroupListContract.Event.OnShareClick(group))
                    }
                )
                is GroupListTitleUI -> AverageTitle(
                    text = stringResource(id = item.valueRes),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun NoRemovableGroupItemRow(
    item: GroupListItemsModel,
    onClicked: () -> Unit,
    onShareClicked: (GroupListItemsModel) -> Unit,
    modifier: Modifier
) {

    Card(
        backgroundColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
            .clickable { onClicked() }
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth(0.80f)
        ) {
            AverageText(
                text = item.getTitle(),
                maxLines = 10,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .weight(weight = 1.0f, fill = true)
                    .align(Alignment.CenterVertically)
            )

            GradientButton(
                onClick = { onShareClicked(item) },
                contentPadding = PaddingValues(0.dp),
                gradient = getStupidLanguageBackgroundRow(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .size(width = 40.dp, height = 32.dp)
                    .align(Alignment.Bottom)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_share),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    contentDescription = null
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun GroupItemRow(
    item: GroupListItemsModel,
    onClicked: () -> Unit,
    dismissState: DismissState,
    onShareClicked: (GroupListItemsModel) -> Unit,
    modifier: Modifier
) {
    SwipeToDismiss(
        state = dismissState,
        dismissThresholds = { direction ->
            FractionalThreshold(0.5f)
        },
        modifier = modifier
            .padding(vertical = 1.dp),
        directions = setOf(DismissDirection.StartToEnd),
        background = {
            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == DismissValue.Default) 0.6f else 2.2f
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "icon",
                    modifier = Modifier.scale(scale)
                )
            }
        }
    ) {
        Card(
            backgroundColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            elevation = animateDpAsState(
                if (dismissState.dismissDirection != null) 8.dp else 4.dp
            ).value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                .clickable { onClicked() }
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                    .fillMaxWidth(0.80f)
            ) {
                AverageText(
                    text = item.getTitle(),
                    maxLines = 10,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .weight(weight = 1.0f, fill = true)
                        .align(Alignment.CenterVertically)
                )

                GradientButton(
                    onClick = { onShareClicked(item) },
                    contentPadding = PaddingValues(0.dp),
                    gradient = getStupidLanguageBackgroundRow(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .size(width = 40.dp, height = 32.dp)
                        .align(Alignment.Bottom)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_share),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}