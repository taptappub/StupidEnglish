package io.taptap.stupidenglish.features.main.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import io.taptap.stupidenglish.base.noRippleClickable
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import io.taptap.stupidenglish.ui.theme.getContentTextColor
import io.taptap.stupidenglish.ui.theme.getTitleTextColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@Composable
fun MainListScreen(
    context: Context,
    state: MainListContract.State,
    effectFlow: Flow<MainListContract.Effect>?,
    onEventSent: (event: MainListContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MainListContract.Effect.Navigation) -> Unit
) {
    ProvideWindowInsets {
        StupidEnglishTheme {
            val scaffoldState: ScaffoldState = rememberScaffoldState()

            // Listen for side effects from the VM
            LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
                effectFlow?.onEach { effect ->
                    when (effect) {
                        is MainListContract.Effect.Navigation.ToAddWord ->
                            onNavigationRequested(effect)
                        is MainListContract.Effect.GetRandomWordsError ->
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = context.getString(effect.errorRes),
                                duration = SnackbarDuration.Short
                            )
                        is MainListContract.Effect.GetWordsError ->
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = context.getString(effect.errorRes),
                                duration = SnackbarDuration.Short
                            )
                        is MainListContract.Effect.Navigation.ToAddSentence ->
                            onNavigationRequested(effect)
                    }
                }?.collect()
            }

            Scaffold(
                scaffoldState = scaffoldState,
                backgroundColor = MaterialTheme.colors.background,
            ) {
                Box {
                    MainList(
                        wordItems = state.mainList,
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
private fun MainList(
    wordItems: List<MainListListModels>,
    onEventSent: (event: MainListContract.Event) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(wordItems) { item ->
            when (item) {
                is NewWordUI -> NewWordItemRow(item = item) {
                    onEventSent(MainListContract.Event.OnAddWordClick)
                }
                is WordListItemUI -> WordItemRow(item = item)
                is WordListTitleUI -> TitleItem(item = item)
                is OnboardingWordUI -> OnboardingItemRow {
                    onEventSent(MainListContract.Event.OnOnboardingClick)
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
                text = stringResource(id = R.string.main_onboarding_text),
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
private fun NewWordItemRow(
    item: NewWordUI,
    onItemClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
            .clickable { onItemClicked() },
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row {
            NewWordItem(
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
    item: WordListItemUI
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
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
private fun NewWordItem(
    item: NewWordUI,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = item.valueRes),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = getContentTextColor(),
            style = MaterialTheme.typography.subtitle1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
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

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    StupidEnglishTheme {
//        MainListScreen(
//            LocalContext.current,
//            MainListContract.State(),
//            null,
//            { },
//            { })
//    }
//}

@Preview(showBackground = true)
@Composable
fun DefaultOnboardingItemRowPreview() {
    StupidEnglishTheme {
        OnboardingItemRow {}
    }
}

