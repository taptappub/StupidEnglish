package io.taptap.stupidenglish.features.sentences.ui

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.ui.DialogSheetScreen
import io.taptap.stupidenglish.ui.EmptyListContent
import io.taptap.stupidenglish.ui.Fab
import io.taptap.stupidenglish.ui.theme.Blue100
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun SentencesListScreen(
    context: Context,
    state: SentencesListContract.State,
    effectFlow: Flow<SentencesListContract.Effect>?,
    onEventSent: (event: SentencesListContract.Event) -> Unit,
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
                is SentencesListContract.Effect.ShowUnderConstruction ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(R.string.under_construction),
                        duration = SnackbarDuration.Short
                    )
            }
        }?.collect()
    }

    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                onEventSent(SentencesListContract.Event.OnMotivationCancel)
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
                modifier = Modifier.fillMaxWidth(),
                onOkButtonClick = {
                    onEventSent(SentencesListContract.Event.OnMotivationConfirmClick)
                },
                onCancelButtonClick = {
                    onEventSent(SentencesListContract.Event.OnMotivationDeclineClick)
                }
            )
        },
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colors.background,
        ) {
            Box {
                val listState = rememberLazyListState()

                SentencesList(
                    sentencesItems = state.sentenceList,
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
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SentencesList(
    sentencesItems: List<SentencesListListModels>,
    onEventSent: (event: SentencesListContract.Event) -> Unit,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = sentencesItems,
            key = { it.id }
        ) { item ->
            val dismissState = rememberDismissState()
            if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                onEventSent(SentencesListContract.Event.OnSentenceDismiss(item as SentencesListItemUI))
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
                is SentencesListTitleUI -> SentenceTitleItem(item = item)
                is SentencesListEmptyUI -> EmptyListContent(
                    description = stringResource(id = item.descriptionRes),
                    modifier = Modifier.height(600.dp)
                )
            }
        }
    }
}

@Composable
fun SentenceTitleItem(
    item: SentencesListTitleUI
) {
    Text(
        text = stringResource(id = item.valueRes),
        textAlign = TextAlign.Left,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.subtitle1,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
    )
}

@ExperimentalMaterialApi
@Composable
fun SentenceItemRow(
    item: SentencesListItemUI,
    onClicked: () -> Unit,
    dismissState: DismissState,
    onShareClicked: (SentencesListItemUI) -> Unit,
    modifier: Modifier
) {
    SwipeToDismiss(
        state = dismissState,
        dismissThresholds = { direction ->
            FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.25f else 0.5f)
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
        ConstraintLayout {
            val (button, card) = createRefs()
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                    .clickable { onClicked() }
                    .constrainAs(card) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Row {
                    SentenceItem(
                        item = item,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 12.dp
                            )
                            .fillMaxWidth(0.80f)
                    )
                }
            }
            Button(
                onClick = { onShareClicked(item) },
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue100),
                modifier = Modifier
                    .size(width = 40.dp, height = 32.dp)
                    .padding(0.dp)
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom, margin = (6).dp)
                        end.linkTo(parent.end, margin = (10).dp)
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_share_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun SentenceItem(
    item: SentencesListItemUI,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = item.sentence,
            textAlign = TextAlign.Left,
            fontSize = 16.sp,
            style = MaterialTheme.typography.subtitle1,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
fun LoadingBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(color = MaterialTheme.colors.secondary)
    }
}

@Preview(showBackground = true)
@Composable
fun SentenceItemPreview() {
    StupidEnglishTheme {

    }
}
