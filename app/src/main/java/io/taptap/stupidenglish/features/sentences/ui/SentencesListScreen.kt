package io.taptap.stupidenglish.features.sentences.ui

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.uikit.*
import io.taptap.uikit.theme.StupidEnglishTheme
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import io.taptap.uikit.theme.getStupidLanguageBackgroundRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun SentencesListScreen(
    context: Context,
    state: SentencesListContract.State,
    effectFlow: Flow<SentencesListContract.Effect>?,
    onEventSent: (event: SentencesListContract.Event) -> Unit,
    onChangeBottomSheetVisibility: (visibility: Boolean) -> Unit,
    onNavigationRequested: (navigationEffect: SentencesListContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val scope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is SentencesListContract.Effect.HideMotivation ->
                    modalBottomSheetState.hideSheet(scope)
                is SentencesListContract.Effect.ShowMotivation ->
                    modalBottomSheetState.showSheet(scope)
                is SentencesListContract.Effect.GetRandomWordsError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(effect.errorRes),
                        duration = SnackbarDuration.Short
                    )
                is SentencesListContract.Effect.GetSentencesError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(effect.errorRes),
                        duration = SnackbarDuration.Short
                    )
                is SentencesListContract.Effect.Navigation.ToAddSentence ->
                    onNavigationRequested(effect)
                is SentencesListContract.Effect.ShowUnderConstruction -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(R.string.under_construction),
                        duration = SnackbarDuration.Short
                    )
                }
                is SentencesListContract.Effect.ChangeBottomBarVisibility -> {
                    onChangeBottomSheetVisibility(effect.isShown)
                }
                is SentencesListContract.Effect.ShowRecover -> {
                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(R.string.stns_delete_message),
                        duration = SnackbarDuration.Short,
                        actionLabel = context.getString(R.string.stns_recover)
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> onEventSent(SentencesListContract.Event.OnApplySentenceDismiss)
                        SnackbarResult.ActionPerformed -> onEventSent(SentencesListContract.Event.OnRecover)
                    }
                }
            }
        }?.collect()
    }

    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        val keyboardController = LocalSoftwareKeyboardController.current
        DisposableEffect(Unit) {
            onDispose {
                onEventSent(SentencesListContract.Event.OnMotivationCancel)
                keyboardController?.hide()
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            DialogSheetScreen(
                painter = painterResource(R.drawable.ic_mail),
                title = stringResource(id = R.string.adds_motivation_title),
                message = stringResource(id = R.string.adds_motivation_message),
                okButtonText = stringResource(id = R.string.adds_motivation_confirm),
                cancelButtonText = stringResource(id = R.string.adds_motivation_decline),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding(),
                onOkButtonClick = {
                    onEventSent(SentencesListContract.Event.OnMotivationConfirmClick)
                },
                onCancelButtonClick = {
                    onEventSent(SentencesListContract.Event.OnMotivationDeclineClick)
                }
            )
        }
    ) {
        StupidEnglishScaffold(
            scaffoldState = scaffoldState
        ) {
            ContentScreen(
                state,
                onEventSent
            )
        }
    }
}

@ExperimentalFoundationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ContentScreen(
    state: SentencesListContract.State,
    onEventSent: (event: SentencesListContract.Event) -> Unit
) {
    StupidLanguageBackgroundBox {
        val listState = rememberLazyListState()

        SentencesList(
            sentencesItems = state.sentenceList,
            deletedSentenceIds = state.deletedSentenceIds,
            listState = listState,
            onEventSent = onEventSent
        )
        if (state.isLoading) {
            LoadingBar()
        }
        Fab(
            extended = listState.firstVisibleItemIndex == 0,
            modifier = Modifier.align(Alignment.BottomEnd),
            iconRes = R.drawable.ic_plus,
            text = stringResource(id = R.string.stns_fab_text),
            onFabClicked = { onEventSent(SentencesListContract.Event.OnAddSentenceClick) }
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun SentencesList(
    sentencesItems: List<SentencesListListModels>,
    onEventSent: (event: SentencesListContract.Event) -> Unit,
    listState: LazyListState,
    deletedSentenceIds: MutableList<Long>
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            top = WindowInsets.navigationBars.getTop(LocalDensity.current).dp,
            bottom = WindowInsets.navigationBars.getBottom(LocalDensity.current).dp + 12.dp + BOTTOM_BAR_MARGIN
        )
    ) {
        items(
            items = sentencesItems,
            key = { it.id}
        ) { item ->
            val dismissState = rememberDismissState()
            if (deletedSentenceIds.contains(item.id)) { //todo выделить в отдельный класс с возможностью удалять?
                if (dismissState.currentValue != DismissValue.Default) {
                    LaunchedEffect(Unit) {
                        dismissState.reset()
                        onEventSent(SentencesListContract.Event.OnRecovered(item))
                    }
                }
            } else {
                if (item is SentencesListItemUI) {
                    if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                        onEventSent(SentencesListContract.Event.OnSentenceDismiss(item))
                    }
                }
            }

            when (item) {
                is SentencesListItemUI -> SentenceItemRow(
                    item = item,
                    dismissState = dismissState,
                    modifier = Modifier.animateItemPlacement(),
                    onClicked = {
                        onEventSent(SentencesListContract.Event.OnSentenceClick)
                    },
                    onShareClicked = { sentence ->
                        onEventSent(SentencesListContract.Event.OnShareClick(sentence))
                    }
                )
                is SentencesListTitleUI -> AverageTitle(
                    text = stringResource(id = item.valueRes),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )
                is SentencesListEmptyUI -> EmptyListContent(
                    title = stringResource(id = R.string.word_empty_list_title),
                    description = stringResource(id = item.descriptionRes),
                    modifier = Modifier.height(300.dp)
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SentenceItemRow(
    item: SentencesListItemUI,
    onClicked: () -> Unit,
    dismissState: DismissState,
    onShareClicked: (SentencesListItemUI) -> Unit,
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
                    text = item.sentence,
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
                        painter = painterResource(id = R.drawable.ic_share_24),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientButton(
    gradient: Brush,
    contentPadding: PaddingValues,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    content: @Composable (RowScope.() -> Unit)
) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = contentPadding,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        onClick = { onClick() },
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(gradient)
                .then(modifier),
        ) {
            content()
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

@Preview(showBackground = true)
@Composable
fun SentenceItemPreview() {
    StupidEnglishTheme {

    }
}
