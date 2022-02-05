package io.taptap.stupidenglish.features.sentences.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.ProvideWindowInsets
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.ui.BottomSheetScreen
import io.taptap.stupidenglish.ui.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@ExperimentalMaterialApi
@Composable
fun SentencesListScreen(
    context: Context,
    state: SentencesListContract.State,
    effectFlow: Flow<SentencesListContract.Effect>?,
    onEventSent: (event: SentencesListContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: SentencesListContract.Effect.Navigation) -> Unit
) {
    ProvideWindowInsets {
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        val scope = rememberCoroutineScope()

        val modalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
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
                MotivationBottomSheetScreen(
                    onEventSent = onEventSent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
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
                    SentencesList(
                        sentencesItems = state.sentenceList,
                        onEventSent = onEventSent
                    )
                    if (state.isLoading) {
                        LoadingBar()
                    }
                }
            }
        }
    }
}

@Composable
private fun MotivationBottomSheetScreen(
    modifier: Modifier,
    onEventSent: (event: SentencesListContract.Event) -> Unit
) {
    BottomSheetScreen(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_pen),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.Center)
                )
            }

            Text(
                text = stringResource(id = R.string.adds_motivation_title),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(
                    Font(R.font.rubik_regular, FontWeight.Normal),
                    Font(R.font.rubik_medium, FontWeight.Medium),
                    Font(R.font.rubik_bold, FontWeight.Bold)
                ),
                color = getTitleTextColor(),
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            Text(
                text = stringResource(id = R.string.adds_motivation_message),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = getContentTextColor(),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 44.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = getSecondaryButtonBackgroundColor()),
                    onClick = {
                        onEventSent(SentencesListContract.Event.OnMotivationDeclineClick)
                    }) {
                    Text(
                        text = stringResource(id = R.string.adds_motivation_decline),
                        color = Black200,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = getPrimaryButtonBackgroundColor()),
                    onClick = {
                        onEventSent(SentencesListContract.Event.OnMotivationConfirmClick)
                    }) {
                    Text(
                        text = stringResource(id = R.string.adds_motivation_confirm),
                        color = White100
                    )
                }
            }
        }
    }
}

@Composable
fun SentencesList(
    sentencesItems: List<SentencesListListModels>,
    onEventSent: (event: SentencesListContract.Event) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(sentencesItems) { item ->
            when (item) {
                is SentencesListNewSentenceUI -> NewSentenceItemRow(item = item) {
                    onEventSent(SentencesListContract.Event.OnAddSentenceClick)
                }
                is SentencesListItemUI -> SentenceItemRow(item = item) { sentence ->
                    onEventSent(SentencesListContract.Event.OnShareClick(sentence))
                }
                is SentencesListTitleUI -> SentenceTitleItem(item = item)
            }
        }
    }
}

@Composable
fun NewSentenceItemRow(
    item: SentencesListNewSentenceUI,
    onItemClicked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
            .clickable { onItemClicked() }
    ) {
        Row {
            NewSentenceItem(
                item = item,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 22.dp,
                        bottom = 22.dp
                    )
                    .fillMaxWidth(0.80f)
                    .align(Alignment.CenterVertically)
            )
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
        color = getTitleTextColor(),
        style = MaterialTheme.typography.subtitle1,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
    )
}

@Composable
fun SentenceItemRow(
    item: SentencesListItemUI,
    onShareClicked: (SentencesListItemUI) -> Unit
) {
    ConstraintLayout {
        val (button, card) = createRefs()
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
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

@Composable
fun NewSentenceItem(
    item: SentencesListNewSentenceUI,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = item.valueRes),
            fontSize = 16.sp,
            color = getContentTextColor(),
            style = MaterialTheme.typography.subtitle1
        )
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
            color = getTitleTextColor(),
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
        SentenceItemRow(
            SentencesListItemUI(
                id = 0,
                sentence = "Some long long long long sentence for testing only",
            ),
            {}
        )
    }
}
