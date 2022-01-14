package io.taptap.stupidenglish.features.sentences.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import io.taptap.stupidenglish.ui.theme.Blue100
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import io.taptap.stupidenglish.ui.theme.getContentTextColor
import io.taptap.stupidenglish.ui.theme.getTitleTextColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


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

        // Listen for side effects from the VM
        LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
            effectFlow?.onEach { effect ->
                when (effect) {
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
