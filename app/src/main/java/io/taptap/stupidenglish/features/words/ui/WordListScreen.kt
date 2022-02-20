package io.taptap.stupidenglish.features.words.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import io.taptap.stupidenglish.base.noRippleClickable
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.ui.BottomSheetScreen
import io.taptap.stupidenglish.ui.Fab
import io.taptap.stupidenglish.ui.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@ExperimentalMaterialApi
@Composable
fun WordListScreen(
    context: Context,
    state: WordListContract.State,
    effectFlow: Flow<WordListContract.Effect>?,
    onEventSent: (event: WordListContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: WordListContract.Effect.Navigation) -> Unit
) {
    val scope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                onEventSent(WordListContract.Event.OnMotivationCancel)
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
        ProvideWindowInsets {
            StupidEnglishTheme {
                val scaffoldState: ScaffoldState = rememberScaffoldState()

                // Listen for side effects from the VM
                LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
                    effectFlow?.onEach { effect ->
                        when (effect) {
                            is WordListContract.Effect.HideMotivation ->
                                modalBottomSheetState.hideSheet(scope)
                            is WordListContract.Effect.ShowMotivation ->
                                modalBottomSheetState.showSheet(scope)
                            is WordListContract.Effect.Navigation.ToAddWord ->
                                onNavigationRequested(effect)
                            is WordListContract.Effect.GetRandomWordsError ->
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = context.getString(effect.errorRes),
                                    duration = SnackbarDuration.Short
                                )
                            is WordListContract.Effect.GetWordsError ->
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = context.getString(effect.errorRes),
                                    duration = SnackbarDuration.Short
                                )
                            is WordListContract.Effect.Navigation.ToAddSentence ->
                                onNavigationRequested(effect)
                            is WordListContract.Effect.ShowUnderConstruction ->
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.under_construction),
                                    duration = SnackbarDuration.Short
                                )
                        }
                    }?.collect()
                }

                Scaffold(
                    scaffoldState = scaffoldState,
                    backgroundColor = MaterialTheme.colors.background,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(it)
                    ) {
                        val listState = rememberLazyListState()

                        MainList(
                            wordItems = state.wordList,
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
                            text = stringResource(id = R.string.word_fab_text),
                            onFabClicked = { onEventSent(WordListContract.Event.OnAddWordClick) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainList(
    wordItems: List<WordListListModels>,
    onEventSent: (event: WordListContract.Event) -> Unit,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(wordItems) { item ->
            when (item) {
                is WordListItemUI -> WordItemRow(item = item) {
                    onEventSent(WordListContract.Event.OnWordClick)
                }
                is WordListTitleUI -> TitleItem(item = item)
                is OnboardingWordUI -> OnboardingItemRow {
                    onEventSent(WordListContract.Event.OnOnboardingClick)
                }
            }
        }
    }
}

@Composable
private fun OnboardingItemRow(onClicked: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier.noRippleClickable(onClick = onClicked)
    ) {
        val (card, image) = createRefs()
        Card(
            shape = RoundedCornerShape(12.dp),
            backgroundColor = MaterialTheme.colors.secondary,
            elevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                .constrainAs(card) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(
                text = stringResource(id = R.string.word_onboarding_text),
                textAlign = TextAlign.Left,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(
                        bottom = 12.dp,
                        start = 16.dp,
                        top = 12.dp,
                        end = 150.dp
                    )
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_main_onboarding),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(height = 140.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        )
    }
}

@Composable
private fun TitleItem(
    item: WordListTitleUI
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
private fun WordItemRow(
    item: WordListItemUI,
    onClicked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
            .clickable { onClicked() }
    ) {
        Row {
            WordItem(
                item = item,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                    .fillMaxWidth(0.80f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun WordItem(
    item: WordListItemUI,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = item.word,
            textAlign = TextAlign.Left,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
            color = getTitleTextColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = item.description,
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = getContentTextColor(),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
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

@Composable
private fun MotivationBottomSheetScreen(
    modifier: Modifier,
    onEventSent: (event: WordListContract.Event) -> Unit
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
                text = stringResource(id = R.string.word_motivation_title),
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
                text = stringResource(id = R.string.word_motivation_message),
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
                        onEventSent(WordListContract.Event.OnMotivationDeclineClick)
                    }) {
                    Text(
                        text = stringResource(id = R.string.word_motivation_decline),
                        color = Black200,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = getPrimaryButtonBackgroundColor()),
                    onClick = {
                        onEventSent(WordListContract.Event.OnMotivationConfirmClick)
                    }) {
                    Text(
                        text = stringResource(id = R.string.word_motivation_confirm),
                        color = White100
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultOnboardingItemRowPreview() {
    StupidEnglishTheme {
        OnboardingItemRow {}
    }
}

